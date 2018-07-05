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
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.Utils;

public class SolidityRuling {

  private static final Logger LOG = Loggers.get(SolidityRuling.class);

  private static final String DIR = "solidity-test-sources/src/";
  protected static final String DIFFERENCES = "src/test/resources/differences";

  protected static Map<String, List<File>> filesToAnalyze = new LinkedHashMap<>();

  private SolidityRuling() {
    // ... no reason for a public constructor
  }

  private static final String[] PROJECTS_TO_ANALYZE = {
    "ethereum-api",
    "Random-Files"
  };

  public static String[] getProjects() {
    return PROJECTS_TO_ANALYZE;
  }

  /*
   * public static void collectSolidityFiles() {
   * Arrays.asList(getProjects())
   * .stream()
   * .forEach(projectName -> {
   * try {
   * String path = String.format("%s%s", DIR, projectName);
   * Files.find(Paths.get(path),
   * Integer.MAX_VALUE,
   * (filePath, fileAttr) -> fileAttr.isRegularFile())
   * .filter(file -> file.getFileName().toString().endsWith(".sol"))
   * .forEach(file -> analyzeFile(file.toFile(), projectName));
   * } catch (IOException e) {
   * LOG.debug(e.getMessage(), e);
   * }
   * });
   * }
   */

  public static void collectFiles() {
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
          filesToAnalyze.put(projectName, listFiles);
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      });
  }

  public static void collectFilesWithRecordedIssues() {

    Arrays.asList(getProjects())
      .stream()
      .forEach(projectName -> {
        String path = String.format("%s%s", SolidityRulingIts.RECORD_ISSUES, projectName);
        try {
          Files.find(Paths.get(path),
            Integer.MAX_VALUE,
            (filePath, fileAttr) -> fileAttr.isRegularFile())
            .forEach(path1 -> {
              String actualIssuePath = String.format("%s%s", SolidityRulingIts.ACTUAL_ISSUES,
                path1.toString().replaceAll(SolidityRulingIts.RECORD_ISSUES, ""));
              try {
                if (!FileUtils.contentEquals(new File(path1.toString()), new File(actualIssuePath))) {
                  List<String> lines = Arrays.asList("Differences: " + path1.toString() + " - " + actualIssuePath);
                  Files.write(Paths.get(String.format("%s%s", SolidityRulingIts.RECORD_ISSUES, "differences")), lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
              } catch (IOException e) {
                LOG.debug(e.getMessage(), e);
              }
            });
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      });

  }

  public static void analyzeFiles() {
    filesToAnalyze.forEach((projectName, fileList) -> {
      fileList.stream().forEach(file -> {
        analyzeFile(file, projectName);
      });
    });
  }

  private static void analyzeFile(File file, String projectName) {
    LOG.debug("Analyze File: " + file);
    Collection<IssuableVisitor> visitors = collectVisitors();
    for (IssuableVisitor visitor : visitors) {
      RuleContext ruleContext = new SolidityRulingIts(visitor.getClass().getSimpleName(), file.getPath(), projectName);
      visitor.setRuleContext(ruleContext);
      try {
        SolidityParser parser = Utils.returnParserUnitFromParsedFile(IOUtils.toString(new FileReader(file)));
        visitor.visit(parser.sourceUnit());
      } catch (IOException e) {
        LOG.debug(e.getMessage(), e);
      }
    }
  }

  public static void deletePreviouslyAnalyzedFiles() throws IOException {
    Arrays.asList(getProjects())
      .stream()
      .forEach(projectName -> {
        String path = String.format("%s%s", SolidityRulingIts.RECORD_ISSUES, projectName);
        try {
          Files.find(Paths.get(path),
            Integer.MAX_VALUE,
            (filePath, fileAttr) -> fileAttr.isRegularFile())
            .forEach(t -> {
              try {
                Files.deleteIfExists(t);
              } catch (IOException e) {
                LOG.debug(e.getMessage(), e);
              }
            });
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      });
    Files.deleteIfExists(new File(DIFFERENCES).toPath());

  }

  public static void findDifferences() {
    filesToAnalyze.forEach((projectName, fileList) -> {
      fileList.stream().forEach(file -> {
        String projectFileName = file.toString().replaceAll(DIR, "");
        String actualIssues = SolidityRulingIts.ACTUAL_ISSUES + projectFileName;
        String recordedIssues = SolidityRulingIts.RECORD_ISSUES + projectFileName;
        try {
          System.out.println(recordedIssues + " - " + actualIssues);
          System.out.println(FileUtils.contentEquals(new File(recordedIssues), new File(actualIssues)));
          if (!FileUtils.contentEquals(new File(recordedIssues), new File(actualIssues))) {
            System.out.println("NOT EQUALLLLLLLLLLLLLLLLLLLLLLLLl");
            List<String> lines = Arrays.asList("Differences: " + recordedIssues + " - " + actualIssues);
            Files.write(Paths.get(String.format("%s%s", SolidityRulingIts.RECORD_ISSUES, "differences")), lines, StandardCharsets.UTF_8,
              StandardOpenOption.CREATE, StandardOpenOption.APPEND);
          }
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      });
    });

  }

  private static Collection<IssuableVisitor> collectVisitors() {
    ActiveRules activeRules = activeRules();
    CheckFactory checkFactory = new CheckFactory(activeRules);
    return checkFactory.<IssuableVisitor>create(SolidityRulesDefinition.REPO_KEY)
      .addAnnotatedChecks((Iterable) CheckList.returnChecks())
      .all();
  }

  private static ActiveRules activeRules() {
    return (new ActiveRulesBuilder())
      .create(RuleKey.of(SolidityRulesDefinition.REPO_KEY, "ExternalRule1"))
      .activate()
      .create(RuleKey.of(SolidityRulesDefinition.REPO_KEY, "ExternalRule2"))
      .activate()
      .build();
  }
}
