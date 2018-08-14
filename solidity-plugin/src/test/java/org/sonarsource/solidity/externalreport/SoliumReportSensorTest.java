package org.sonarsource.solidity.externalreport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.ExternalIssue;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.RuleType;

import static org.assertj.core.api.Assertions.assertThat;

public class SoliumReportSensorTest {

  static final Path REPORT_BASE_PATH = Paths.get("src", "test", "resources", "externalreports").toAbsolutePath();

  @Test
  public void test_descriptor() {
    DefaultSensorDescriptor sensorDescriptor = new DefaultSensorDescriptor();
    new SoliumReportSensor().describe(sensorDescriptor);
    assertThat(sensorDescriptor.name()).isEqualTo("Import of Solium issues.");
    assertThat(sensorDescriptor.languages()).containsOnly("solidity");
  }

  @Test
  public void no_issues_with_sonarqube_71() throws IOException {
    SensorContextTester context = SoliumTestHelper.createContext(7, 1);
    context.settings().setProperty(SoliumReportSensor.PROPERTY_KEY, REPORT_BASE_PATH.resolve("test-report1.out").toString());
    List<ExternalIssue> externalIssues = SoliumTestHelper.executeSensor(new SoliumReportSensor(), context);
    assertThat(externalIssues).isEmpty();
  }

  @Test
  public void test_issues() throws IOException {
    SensorContextTester context = SoliumTestHelper.createContext(7, 2);
    context.settings().setProperty(SoliumReportSensor.PROPERTY_KEY, REPORT_BASE_PATH.resolve("test-report1.out").toString());
    List<ExternalIssue> externalIssues = SoliumTestHelper.executeSensor(new SoliumReportSensor(), context);
    assertThat(externalIssues).isNotEmpty();
    ExternalIssue firstExternalIssue = externalIssues.get(0);
    assertThat(firstExternalIssue.ruleKey()).isEqualTo(RuleKey.parse("solium:security/no-block-members"));
    assertThat(firstExternalIssue.type()).isEqualTo(RuleType.CODE_SMELL);
    assertThat(firstExternalIssue.severity()).isEqualTo(Severity.MAJOR);
    assertThat(firstExternalIssue.primaryLocation().message()).isEqualTo("Avoid using 'now' (alias to 'block.timestamp').");
    assertThat(firstExternalIssue.primaryLocation().textRange().start().line()).isEqualTo(6);
  }

  @Test
  public void no_issues_without_solium_property() throws IOException {
    SensorContextTester context = SoliumTestHelper.createContext(7, 2);
    List<ExternalIssue> externalIssues = SoliumTestHelper.executeSensor(new SoliumReportSensor(), context);
    assertThat(externalIssues).isEmpty();
  }

  @Test
  public void simple_issue() throws IOException {
    String line = "21:26 error  Assignment operator must have exactly single space on both sides of it.    operator-whitespace";
    org.sonarsource.solidity.externalreport.ExternalIssue issue = new SoliumReportSensor().parse(line);
    assertThat(issue).isNotNull();
    assertThat(issue.message).isEqualTo("Assignment operator must have exactly single space on both sides of it.");
    assertThat(issue.lineNumber).isEqualTo(21);
    assertThat(issue.ruleKey).isEqualTo("operator-whitespace");
    assertThat(issue.type).isEqualTo(RuleType.BUG);

    line = "21:26 error  Assignment operator must have exactly single space on both sides of it.    security/no-block-members";
    issue = new SoliumReportSensor().parse(line);
    assertThat(issue).isNotNull();

    line = "21:26 warning   Ensure that all import statements are on top of the file   imports-on-top";
    issue = new SoliumReportSensor().parse(line);
    assertThat(issue).isNotNull();

  }

  @Test
  public void wrong_issue() throws IOException {
    String line = " warning  Wrong Issue    nokey";
    org.sonarsource.solidity.externalreport.ExternalIssue issue = new SoliumReportSensor().parse(line);
    assertThat(issue).isNull();
  }

  @Test
  public void wrong_issue2() throws IOException {
    String line = "2:0 warning  Assignment operator must have exactly single space on both sides of it.   wrong_key";
    org.sonarsource.solidity.externalreport.ExternalIssue issue = new SoliumReportSensor().parse(line);
    assertThat(issue).isNull();
  }

  @Test
  public void wrong_file() throws IOException {
    SensorContextTester context = SoliumTestHelper.createContext(7, 2);
    context.settings().setProperty(SoliumReportSensor.PROPERTY_KEY, REPORT_BASE_PATH.resolve("wrong-file.out").toString());
    List<ExternalIssue> externalIssues = SoliumTestHelper.executeSensor(new SoliumReportSensor(), context);
    assertThat(externalIssues).isEmpty();
  }

  @Test
  public void test_no_input() throws IOException {
    AbstractExternalReportSensor.getIOFile(new File(""), "./");
    SoliumReportSensor sensor = new SoliumReportSensor();
    SensorContextTester context = SoliumTestHelper.createContext(7, 2);
    org.sonarsource.solidity.externalreport.ExternalIssue issue = new org.sonarsource.solidity.externalreport.ExternalIssue(SoliumReportSensor.LINTER_ID, RuleType.CODE_SMELL,
      "", "", 0, "");
    sensor.addLineIssue(context, issue);
  }
}
