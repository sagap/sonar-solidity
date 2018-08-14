package org.sonarsource.solidity.externalreport;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.annotation.Nullable;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewExternalIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.utils.Version;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.Solidity;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class AbstractExternalReportSensor implements Sensor {

  protected static final Logger LOG = Loggers.get(AbstractExternalReportSensor.class);

  static final Severity DEFAULT_SEVERITY = Severity.MAJOR;
  static final String GENERIC_ISSUE_KEY = "issue";
  static final long DEFAULT_REMEDIATION_COST = 5L;

  abstract String linterName();

  abstract String reportsPropertyName();

  @Nullable
  abstract ExternalIssue parse(String line);

  protected String logPrefix() {
    return this.getClass().getSimpleName() + ": ";
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .onlyOnLanguage("solidity")
      .onlyWhenConfiguration(configuration -> configuration.hasKey(reportsPropertyName()))
      .name("Import of " + linterName() + " issues.");
  }

  @Override
  public void execute(SensorContext sensorContext) {
    boolean externalIssuesSupported = sensorContext.getSonarQubeVersion().isGreaterThanOrEqual(Version.create(7, 2));
    String[] reportPaths = sensorContext.config().getStringArray(reportsPropertyName());
    if (reportPaths.length == 0) {
      return;
    }

    if (!externalIssuesSupported) {
      LOG.error("Import of external issues requires SonarQube 7.2 or greater.");
      return;
    }

    for (String reportPath : reportPaths) {
      File report = getIOFile(sensorContext.fileSystem().baseDir(), reportPath);
      importReport(sensorContext, report);
    }
  }

  private void importReport(SensorContext context, File report) {
    try {
      String filename = null;
      LOG.info(logPrefix() + "Importing {}", report.getPath());
      for (String line : Files.readAllLines(report.toPath(), UTF_8)) {
        if (!line.isEmpty()) {
          ExternalIssue issue = parse(line);
          if (issue != null) {
            if (issue.message.equals("onlyfilename")) {
              filename = issue.filename;
            } else {
              issue.filename = filename;
              addLineIssue(context, issue);
            }
          }
        }
      }
    } catch (IOException e) {
      LOG.error(logPrefix() + "No issues information will be saved as the report file '{}' can't be read.",
        report.getPath(), e);
    }
  }

  static File getIOFile(File baseDir, String path) {
    File file = new File(path);
    if (!file.isAbsolute()) {
      file = new File(baseDir, path);
    }
    return file;
  }

  InputFile getInputFile(SensorContext context, String filePath) {
    FilePredicates predicates = context.fileSystem().predicates();
    String[] arr = filePath.split("/");
    filePath = arr[arr.length - 1];
    InputFile inputFile = context.fileSystem().inputFile(predicates.or(predicates.hasRelativePath(filePath), predicates.hasAbsolutePath(filePath)));
    if (inputFile == null) {
      LOG.warn(logPrefix() + "No input file found for {}. No {} issues will be imported on this file.", filePath, linterName());
      return null;
    }
    return inputFile;
  }

  void addLineIssue(SensorContext context, ExternalIssue issue) {
    InputFile inputFile = getInputFile(context, issue.filename);
    if (inputFile != null) {
      NewExternalIssue newExternalIssue = context.newExternalIssue();
      NewIssueLocation primaryLocation = newExternalIssue.newLocation()
        .message(issue.message)
        .on(inputFile)
        .at(inputFile.selectLine(issue.lineNumber));

      newExternalIssue
        .at(primaryLocation)
        .forRule(RuleKey.of(issue.linter, issue.ruleKey))
        .type(issue.type)
        .severity(DEFAULT_SEVERITY)
        .remediationEffortMinutes(DEFAULT_REMEDIATION_COST)
        .save();
    }
  }

  public static void createExternalRuleRepository(Context context, String linterId, String linterName) {
    NewRepository externalRepo = context.createExternalRepository(linterId, Solidity.KEY).setName(linterName);
    String pathToRulesMeta = "org/sonar/l10n/solidity/rules/" + linterId + "/rules.json";

    try (InputStreamReader inputStreamReader = new InputStreamReader(AbstractExternalReportSensor.class.getClassLoader().getResourceAsStream(pathToRulesMeta),
      StandardCharsets.UTF_8)) {
      ExternalRule[] rules = new Gson().fromJson(inputStreamReader, ExternalRule[].class);
      for (ExternalRule rule : rules) {
        NewRule newRule = externalRepo.createRule(rule.key).setName(rule.name);
        newRule.setHtmlDescription(rule.description);
        newRule.setDebtRemediationFunction(newRule.debtRemediationFunctions().constantPerIssue(DEFAULT_REMEDIATION_COST + "min"));
        if (linterId.equals(SoliumReportSensor.LINTER_ID)) {
          newRule.setType(RuleType.BUG);
        }
      }
    } catch (Exception e) {
      throw new IllegalStateException("Can't read resource: " + pathToRulesMeta, e);
    }
    externalRepo.done();
  }

  private static class ExternalRule {
    String key;
    String name;
    String description;
  }
}
