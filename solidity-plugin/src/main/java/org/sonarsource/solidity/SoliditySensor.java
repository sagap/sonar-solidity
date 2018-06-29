/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.solidity;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.RecognitionException;
import org.sonar.api.SonarProduct;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextRange;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.Version;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.PragmaDirectiveContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.frontend.Utils;

public class SoliditySensor implements Sensor {

  private static final Logger LOG = Loggers.get(SoliditySensor.class);

  private final FileLinesContextFactory fileLinesContextFactory;
  public static final Version SQ_VERSION = Version.create(6, 7);

  protected static final String REPORT_PATH_KEY = "sonar.solidity.reportPath";

  protected CognitiveComplexityVisitor cognitiveComplexity;
  // protected final Configuration config;

  public static final ImmutableList<String> KEYWORDS = ImmutableList.<String>builder()
    .add(SolidityKeywords.get()).build();

  public static final ImmutableList<String> KEYWORD_TYPES = ImmutableList.<String>builder()
    .add(SolidityKeywords.getKeyowrdTypes()).build();

  public SoliditySensor(FileLinesContextFactory fileLinesContextFactory) {
    this.fileLinesContextFactory = fileLinesContextFactory;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .onlyOnLanguage(Solidity.KEY)
      .name("SonarSolidity")
      .onlyOnFileType(Type.MAIN);
  }

  protected String reportPathKey() {
    return REPORT_PATH_KEY;
  }

  @Override
  public void execute(SensorContext context) {
    FileSystem fileSystem = context.fileSystem();

    FilePredicate mainFilePredicate = fileSystem.predicates().and(
      fileSystem.predicates().hasType(InputFile.Type.MAIN),
      fileSystem.predicates().hasLanguage(Solidity.KEY));

    List<InputFile> inputFiles = new ArrayList<>();
    fileSystem.inputFiles(mainFilePredicate).forEach(inputFiles::add);
    analyzeFiles(context, inputFiles);
  }

  public void analyzeFiles(SensorContext context, List<InputFile> files) {
    for (InputFile file : files) {
      String lastAnalyzedFile = file.toString();
      if (inSonarQube(context)) {
        try {
          LOG.debug("Analyzing: " + lastAnalyzedFile);
          SolidityParser parser = Utils.returnParserUnitFromParsedFile(file.contents());
          getSyntaxHighlighting(parser, context, file).save();
          saveFileMeasures(context, computeMeasures(parser, fileLinesContextFactory.createFor(file), file), file);
          saveIssues(context, file, Utils.returnParserUnitFromParsedFile(file.contents()));
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      } else {
        LOG.debug(lastAnalyzedFile);
      }
    }
  }

  private void saveIssues(SensorContext context, InputFile file, SolidityParser parser) {
    // List Issues here!
    // IssuableVisitor.reportTest(file, context, parser.sourceUnit());
    SourceUnitContext suc = parser.sourceUnit();
    PragmaDirectiveContext pragma = suc.pragmaDirective(0);
    List<NewIssue> issues = new ArrayList<>();
    if (pragma != null) {
      RuleKey ruleKey = RuleKey.of("solidity-solidity", "ExampleRule1");
      NewIssue newIssue = context.newIssue().forRule(ruleKey).gap(Double.valueOf(1)).overrideSeverity(Severity.MAJOR);
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
        NewIssue newIssue2 = context.newIssue().forRule(ruleKey2).gap(Double.valueOf(1)).overrideSeverity(Severity.MAJOR);
        NewIssueLocation location2 = newIssue2.newLocation()
          .on(file).message("AAA message");

        IdentifierContext idctx = contract.identifier();
        DefaultTextPointer df12 = new DefaultTextPointer(contract.getStart().getLine(), contract.getStart().getCharPositionInLine());
        DefaultTextPointer df22 = new DefaultTextPointer(idctx.getStop().getLine(), ("contract " + idctx.getText()).length());// idctx.getText().length()

        DefaultTextRange range2 = new DefaultTextRange(df12, df22);
        location2.at(range2);
        newIssue2.at(location2);
        newIssue2.save();
        issues.add(newIssue2);
      }
    }
  }

  private void saveIssue(SensorContext context, InputFile file, SolidityParser parser) {
    RuleKey ruleKey = RuleKey.of("solidity-solidity", "ExampleRule1");

  }

  private FileMeasures computeMeasures(SolidityParser parser, FileLinesContext fileLinesContext, InputFile file) throws RecognitionException, IOException {
    MetricsVisitor metricsVisitor = new MetricsVisitor(parser);

    cognitiveComplexity = new CognitiveComplexityVisitor(Utils.returnParserUnitFromParsedFile(file.contents()).sourceUnit());
    int totalComplexity = cognitiveComplexity.functionsComplexity.values().stream().mapToInt(Integer::intValue).sum();
    metricsVisitor.fileMeasures.setFileCognitiveComplexity(totalComplexity);
    return metricsVisitor.fileMeasures;
  }

  public NewHighlighting getSyntaxHighlighting(SolidityParser parser, SensorContext context, InputFile inputFile) {
    NewHighlighting highlighting = context.newHighlighting().onFile(inputFile);
    SyntaxHighlightingVisitor visitor = new SyntaxHighlightingVisitor(highlighting);
    visitor.visitTokens(parser.getTokenStream());
    visitor.highlightComments(parser);
    return highlighting;
  }

  private static void saveFileMeasures(SensorContext context, FileMeasures fileMeasures, InputFile inputFile) {
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getLinesOfCodeNumber()).forMetric(CoreMetrics.NCLOC).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getCommentLinesNumber()).forMetric(CoreMetrics.COMMENT_LINES).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getContractNumber()).forMetric(CoreMetrics.CLASSES).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getFunctionNumber()).forMetric(CoreMetrics.FUNCTIONS).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getStatementNumber()).forMetric(CoreMetrics.STATEMENTS).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getFileCognitiveComplexity()).forMetric(CoreMetrics.COGNITIVE_COMPLEXITY).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getFileComplexity()).forMetric(CoreMetrics.COMPLEXITY).save();
  }

  private static boolean inSonarQube(SensorContext context) {
    return !context.getSonarQubeVersion().isGreaterThanOrEqual(SQ_VERSION)
      || context.runtime().getProduct() != SonarProduct.SONARLINT;
  }
}
