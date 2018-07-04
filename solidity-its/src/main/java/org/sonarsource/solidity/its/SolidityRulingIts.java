package org.sonarsource.solidity.its;

import org.antlr.v4.runtime.Token;
import org.sonarsource.solidity.checks.RuleContext;

public class SolidityRulingIts implements RuleContext {

  private static final String RECORD_ISSUES = "";
  private String projectDir;

  public SolidityRulingIts(String projectDir) {
    this.projectDir = projectDir;
  }

  @Override
  public void addIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
    reportIssue(start.getLine(), projectDir);
  }

  private void reportIssue(int line, String projectDir) {
    System.out.println(projectDir);
  }

}
