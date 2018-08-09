package org.sonarsource.solidity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextRange;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.Version;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.PragmaDirectiveContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.frontend.SolidityParsingPhase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SoliditySensorTest {

  private static final Version SONARLINT_DETECTABLE_VERSION = Version.create(6, 7);
  private static final SonarRuntime SONARQUBE_6_7 = SonarRuntimeImpl.forSonarQube(Version.create(6, 7), SonarQubeSide.SCANNER);
  private static final SonarRuntime SONARLINT_RUNTIME = SonarRuntimeImpl.forSonarLint(SONARLINT_DETECTABLE_VERSION);

  private Path workDir;
  private Path projectDir;
  private SensorContextTester sensorContext = SensorContextTester.create(new File("src/test/resources").getAbsoluteFile());

  private FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
  private CheckFactory checkFactory = new CheckFactory(mock(ActiveRules.class));

  private static final Logger LOG = Loggers.get(SoliditySensorTest.class);

  private SoliditySensor createSensor(CheckFactory checkFactory) {
    return new SoliditySensor(checkFactory == null ? new CheckFactory(mock(ActiveRules.class)) : checkFactory, createFileLinesContextFactory());
  }

  private FileLinesContextFactory createFileLinesContextFactory() {
    FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
    FileLinesContext fileLinesContext = mock(FileLinesContext.class);
    when(fileLinesContextFactory.createFor(any(InputFile.class))).thenReturn(fileLinesContext);
    return fileLinesContextFactory;
  }

  @Test
  public void test_description() {
    DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    sensor.describe(descriptor);
    assertThat(descriptor.name()).isEqualTo("SonarSolidity");
    assertThat(descriptor.languages()).containsOnly("solidity");
  }

  @Test
  public void highlight_comment() {
    String filename = "test.sol";
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 2, 14)).first().isEqualTo(TypeOfText.STRUCTURED_COMMENT);
  }

  @Test
  public void test_file() {
    String filename = "test1.sol";
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);

    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 5, 52)).isNotEmpty();
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 1, 4)).isNotEmpty();
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 1, 0)).first().isEqualTo(TypeOfText.KEYWORD);
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 11, 18)).first().isEqualTo(TypeOfText.CONSTANT);
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 6, 35)).first().isEqualTo(TypeOfText.STRING);
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 11, 12)).isNotEmpty();
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 11, 12)).first().isEqualTo(TypeOfText.KEYWORD_LIGHT);
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 23, 13)).first().isEqualTo(TypeOfText.STRUCTURED_COMMENT);
    assertThat(sensorContext.highlightingTypeAt("module:" + filename, 5, 43)).first().isEqualTo(TypeOfText.COMMENT);
  }

  @Test
  public void test_complexity() {
    String filename = "test3.sol";
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);

  }

  @Test
  public void test_two_contracts() {
    String filename = "test_contracts.sol";
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
  }

  @Test
  public void test_cognitive_complexity1() throws IOException {
    String filename = "test_cognitive_complexity1.sol";
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensor.cognitiveComplexity.getCognitiveComplexity()).isEqualTo(2);
  }

  @Test
  public void test_cognitive_complexity2() throws IOException {
    String filename = "test_cognitive_complexity2.sol";
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensor.cognitiveComplexity.getCognitiveComplexity()).isEqualTo(5);
  }

  @Test
  public void test_cognitive_complexity3() throws IOException {
    String filename = "test_cognitive_complexity3.sol";
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensor.cognitiveComplexity.getCognitiveComplexity()).isEqualTo(11);
  }

  @Test
  public void test_cognitive_complexity4() throws IOException {
    String filename = "test_cognitive_complexity4.sol";
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensor.cognitiveComplexity.getCognitiveComplexity()).isEqualTo(17);
  }

  @Test
  public void test_cognitive_complexity5() throws IOException {
    String filename = "test_cognitive_complexity5.sol";
    CheckFactory checkFactory = new CheckFactory(activeRuleForVersion());
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensor.cognitiveComplexity.getCognitiveComplexity()).isEqualTo(14);
  }

  @Test
  public void test_cognitive_complexity6() throws IOException {
    String filename = "test_cognitive_complexity6.sol";
    CheckFactory checkFactory = new CheckFactory(activeRuleForVersion());
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensor.cognitiveComplexity.getCognitiveComplexity()).isEqualTo(24);
    Collection<Issue> issues = sensorContext.allIssues();
    assertThat(issues).hasSize(0);
  }

  @Test
  public void test_cognitive_complexity7() throws IOException {
    String filename = "test_ternary.sol";
    CheckFactory checkFactory = new CheckFactory(activeRuleForVersion());
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensor.cognitiveComplexity.getCognitiveComplexity()).isEqualTo(3);
  }

  @Test
  public void test_cognitive_complexity8() throws IOException {
    String filename = "test_cognitive_complexity8.sol";
    CheckFactory checkFactory = new CheckFactory(activeRuleForVersion());
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensor.cognitiveComplexity.getCognitiveComplexity()).isEqualTo(21);
    Collection<Issue> issues = sensorContext.allIssues();
    assertThat(issues).hasSize(1);
  }

  @Test
  public void test_rule_context() {
    String filename = "test_cognitive_complexity8.sol";
    CheckFactory checkFactory = new CheckFactory(activeFirstRules());
    SoliditySensor sensor = new SoliditySensor(checkFactory, createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
    assertThat(sensorContext.allIssues()).hasSize(2);
    SolidityRuleContext ruleContext = new SolidityRuleContext(createInputFile(filename), sensorContext);
    assertThat(ruleContext).isNotNull();
  }

  @Test
  public void test_reporting() throws IOException {
    String filename = "test_issues1.sol";
    InputFile inputFile = createInputFile(filename);
    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(inputFile.contents());
    List<NewIssue> issues = createDummyIssuesPragmaAndContract(inputFile, sensorContext, suc);
    assertThat(issues).hasSize(2);
  }

  private void analyseSingleFile(SoliditySensor sensor, String filename) {
    InputFile file = createInputFile(filename);
    sensorContext.fileSystem().add(file);
    sensor.execute(sensorContext);
  }

  private InputFile createInputFile(String filename) {
    try {
      return TestInputFileBuilder.create("module", filename)
        .setModuleBaseDir(getModuleBaseDir().toPath())
        .setCharset(StandardCharsets.UTF_8)
        .setLanguage(Solidity.KEY)
        .initMetadata(new String(java.nio.file.Files.readAllBytes(new File("src/test/resources/" + filename).toPath()), StandardCharsets.UTF_8)).build();
    } catch (java.io.IOException e) {
      throw new IllegalStateException("File Not Found!", e);
    }
  }

  private List<NewIssue> createDummyIssuesPragmaAndContract(InputFile file, SensorContext context, SourceUnitContext suc) {
    PragmaDirectiveContext pragma = suc.pragmaDirective(0);
    List<NewIssue> issues = new ArrayList<>();
    if (pragma != null) {
      RuleKey ruleKey = RuleKey.of("solidity-solidity", "ExampleRule1");
      NewIssue newIssue = context.newIssue().forRule(ruleKey);
      NewIssueLocation location = newIssue.newLocation()
        .on(file).message("AAA message");
      DefaultTextPointer df1 = new DefaultTextPointer(pragma.getStart().getLine(), pragma.getStart().getCharPositionInLine());
      DefaultTextPointer df2 = new DefaultTextPointer(pragma.getStop().getLine(), pragma.getStop().getCharPositionInLine());
      DefaultTextRange range = new DefaultTextRange(df1, df2);
      location.at(range);
      newIssue.at(location);
      newIssue.save();
      issues.add(newIssue);
      ContractDefinitionContext contract = suc.contractDefinition(0);
      if (contract != null) {
        RuleKey ruleKey2 = RuleKey.of("solidity-solidity", "ExampleRule1");
        NewIssue newIssue2 = context.newIssue().forRule(ruleKey2);
        NewIssueLocation location2 = newIssue2.newLocation()
          .on(file).message("AAA message");
        DefaultTextPointer df12 = new DefaultTextPointer(pragma.getStart().getLine(), pragma.getStart().getCharPositionInLine());
        DefaultTextPointer df22 = new DefaultTextPointer(pragma.getStop().getLine(), pragma.getStop().getCharPositionInLine());
        DefaultTextRange range2 = new DefaultTextRange(df12, df22);
        location2.at(range2);
        newIssue2.at(location2);
        newIssue2.save();
        issues.add(newIssue2);
      }
    }
    return issues;
  }

  private ActiveRules activeRuleForVersion() {
    return (new ActiveRulesBuilder())
      .create(RuleKey.of(SolidityRulesDefinition.REPO_KEY, "ExternalRule1"))
      .setName("Foo Rule")
      .activate()
      .build();
  }

  private ActiveRules activeFirstRules() {
    return (new ActiveRulesBuilder())
      .create(RuleKey.of(SolidityRulesDefinition.REPO_KEY, "ExternalRule1"))
      .setName("Foo Rule")
      .activate()
      .create(RuleKey.of(SolidityRulesDefinition.REPO_KEY, "ExternalRule2"))
      .setName("Foo Rule2")
      .activate()
      .build();
  }

  public static File getModuleBaseDir() {
    return new File("src/test/resources");
  }
}
