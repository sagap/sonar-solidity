package org.sonarsource.solidity.its;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.checks.CognitiveComplexityCheck;
import org.sonarsource.solidity.checks.IssuableVisitor;
import org.sonarsource.solidity.checks.RuleContext;
import org.sonarsource.solidity.frontend.SolidityParsingPhase;
import static org.assertj.core.api.Assertions.assertThat;

public class SolidityRulingTest {

  private static final Logger LOG = Loggers.get(SolidityRulingTest.class);

  @Test
  public void test() {
    assertThat(SolidityRuling.getProjects()).hasSize(9);
    try {

      createDirectoriesIfNotExist();
      SolidityRuling.collectFilesForIssues();

      SolidityRuling.deletePreviouslyAnalyzedFiles();

      File file = new File(SolidityRuling.DIFFERENCES);
      assertThat(file).doesNotExist();

      SolidityRuling.collectSolidityFiles();

      SolidityRuling.analyzeFiles();
      SolidityRuling.findDifferences();

      assertThat(file).doesNotExist();

      // computeCognitiveComplexity();
    } catch (IOException e) {
      LOG.debug(e.getMessage());
    }
  }

  private static void createDirectoriesIfNotExist() {
    Arrays.asList(SolidityRuling.getProjects())
      .stream()
      .forEach(projectName -> {
        String pathName = String.format("%s%s", SolidityRulingIts.RECORD_ISSUES, projectName);
        final Path path = Paths.get(pathName);
        if (Files.notExists(path)) {
          LOG.error("Error: file does not exist "+pathName);
        }
      });
  }

  /*
   * *
   * for Testing*
   */
  private static void computeCognitiveComplexity() {
    SolidityRuling.filesToAnalyze.forEach((projectName, fileList) -> {
      fileList.stream().forEach(file -> {
        IssuableVisitor visitor = new CognitiveComplexityCheck();
        RuleContext ruleContext = new RuleContextCognitiveComplexity();
        visitor.setRuleContext(ruleContext);
        try {
          SolidityParsingPhase parser = new SolidityParsingPhase();
          visitor.visit(parser.parse(IOUtils.toString(new FileReader(file))));
        } catch (IOException e) {
          LOG.debug(e.getMessage());
        }
      });
    });
  }

  private static class RuleContextCognitiveComplexity implements RuleContext {

    @Override
    public void addIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
    }

    @Override
    public void addIssue(Token start, Token stop, int offset, String reportMessage, String externalRuleKey) {
    }

    @Override
    public void addIssueOnFile(String reportMessage, String externalRuleKey) {
    }

    @Override
    public void addIssue(ParserRuleContext ctx, String reportMessage, String externalRuleKey) {
      // TODO Auto-generated method stub

    }
  }
}
