package org.sonarsource.solidity.its;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.checks.RuleContext;

public class SolidityRulingIts implements RuleContext {

  private static final Logger LOG = Loggers.get(SolidityRulingIts.class);

  public static final String RECORD_ISSUES = "src/test/resources/";
  public static final String ACTUAL_ISSUES = "actual-issues/";

  private String projectDir;
  private String ruleName;
  private String projectName;

  public SolidityRulingIts(String ruleName, String projectDir, String projectName) {
    this.projectDir = projectDir;
    this.ruleName = ruleName;
    this.projectName = projectName;
  }

  @Override
  public void addIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
    reportIssue(start.getLine(), projectDir);
  }

  @Override
  public void addIssueOnFile(String reportMessage, String externalRuleKey) {
    // TODO
  }

  @Override
  public void addIssue(Token start, Token stop, int offset, String reportMessage, String externalRuleKey) {
    reportIssue(start.getLine(), projectDir);
  }

  private void reportIssue(int line, String projectDir) {
    List<String> lines = Arrays.asList(projectDir, "Issue Line: " + line);
    try {
      Files.write(Paths.get(String.format("%s%s%s%s", RECORD_ISSUES, projectName, "/", ruleName)), lines, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }
  }
}
