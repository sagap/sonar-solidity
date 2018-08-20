package org.sonarsource.solidity.its;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.SolidityRulesDefinition;
import org.sonarsource.solidity.checks.CheckList;
import org.sonarsource.solidity.checks.IssuableVisitor;
import org.sonarsource.solidity.checks.RuleContext;
import org.sonarsource.solidity.checks.RuleKeyList;
import org.sonarsource.solidity.frontend.SolidityParsingPhase;

public class SolidityRuling {

  private static final Logger LOG = Loggers.get(SolidityRuling.class);

  private static final String DIR = "solidity-test-sources/src/";
  protected static final String DIFFERENCES = "src/test/resources/differences";

  protected static Map<String, List<File>> filesToAnalyze = new LinkedHashMap<>();
  protected static Map<String, List<File>> filesToCompare = new LinkedHashMap<>();

  private SolidityRuling() {
    // ... no reason for a public constructor
  }

  private static final String[] PROJECTS_TO_ANALYZE = {
    "ethereum-api",
    "Random-Files",
    "aragonOS",
    "ethorse-core",
    "openzeppelin-solidity",
    "kleros-interaction",
    "pm-contracts",
    "augur-core",
    "standard-contracts"
  };

  public static String[] getProjects() {
    return PROJECTS_TO_ANALYZE;
  }

  public static void collectSolidityFiles() {
    Arrays.asList(getProjects())
      .stream()
      .forEach(projectName -> {
        List<File> listFiles = new ArrayList<>();
        try {
          String path = String.format("%s%s", DIR, projectName);
          Files.find(Paths.get(path),
            Integer.MAX_VALUE,
            (filePath, fileAttr) -> fileAttr.isRegularFile())
            .filter(file -> file.getFileName().toString().endsWith(".sol"))
            .forEach(file -> listFiles.add(file.toFile()));
          sortList(listFiles);
          filesToAnalyze.put(projectName, listFiles);
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      });
  }

  public static void findDifferences() {
    filesToCompare.forEach((projectName, recordedIssues) -> {
      recordedIssues.stream()
        .forEach(issues -> {
          String actualIssuePath = String.format("%s%s", SolidityRulingIts.ACTUAL_ISSUES,
            issues.toString().replaceAll(SolidityRulingIts.RECORD_ISSUES, ""));
          try {
            if (!FileUtils.contentEquals(new File(issues.toString()), new File(actualIssuePath))) {
              List<String> lines = Arrays.asList("Differences: " + issues.toString() + " - " + actualIssuePath);
              Files.write(Paths.get(String.format("%s%s", SolidityRulingIts.RECORD_ISSUES, "differences")), lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
          } catch (IOException e) {
            LOG.debug(e.getMessage(), e);
          }

        });
    });
  }

  public static void analyzeFiles() {
    filesToAnalyze.forEach((projectName, fileList) -> {
      fileList.stream().forEach(file -> {
        analyzeFile(file, projectName);
      });
    });
  }

  public static void collectFilesForIssues() {
    Arrays.asList(getProjects())
      .stream()
      .forEach(projectName -> {
        String path = String.format("%s%s", SolidityRulingIts.RECORD_ISSUES, projectName);
        List<File> listFiles = new ArrayList<>();
        try {
          Files.find(Paths.get(path),
            Integer.MAX_VALUE,
            (filePath, fileAttr) -> fileAttr.isRegularFile())
            .forEach(recordedIssues -> listFiles.add(recordedIssues.toFile()));
          filesToCompare.put(projectName, listFiles);
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      });

  }

  private static void analyzeFile(File file, String projectName) {
    LOG.debug("Analyze File: " + file);
    Collection<IssuableVisitor> visitors = collectVisitors();
    for (IssuableVisitor visitor : visitors) {
      RuleContext ruleContext = new SolidityRulingIts(visitor.getClass().getSimpleName(), file.getPath(), projectName);
      visitor.setRuleContext(ruleContext);
      try {
        SolidityParsingPhase parsing = new SolidityParsingPhase();
        visitor.visit(parsing.parse(IOUtils.toString(new FileReader(file))));
      } catch (Exception e) {
        LOG.error(e + ", Visitor: " + visitor.getClass() + " @File: " + file);
        break;
      }
    }
  }

  public static void deletePreviouslyAnalyzedFiles() throws IOException {
    filesToCompare.forEach((projectName, fileList) -> {
      fileList.stream().forEach(file -> {
        try {
          Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      });
    });
    Files.deleteIfExists(new File(DIFFERENCES).toPath());
  }

  private static Collection<IssuableVisitor> collectVisitors() {
    ActiveRules activeRules = activeRules();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    return checkFactory.<IssuableVisitor>create(SolidityRulesDefinition.REPO_KEY)
      .addAnnotatedChecks((Iterable) CheckList.returnChecks())
      .all();
  }

  private static void sortList(List<File> listFiles) {
    Collections.sort(listFiles, (File f1, File f2) -> f1.getName().compareTo(f2.getName()));
  }

  private static ActiveRules activeRules() {
    ActiveRulesBuilder activeRulesBuilder = new ActiveRulesBuilder();
    for (String ruleKey : RuleKeyList.returnChecks()) {
      activeRulesBuilder.create(RuleKey.of(SolidityRulesDefinition.REPO_KEY, ruleKey))
        .activate();
    }
    return activeRulesBuilder.build();
  }
}
