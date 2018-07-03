package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.Token;
import org.sonar.api.rule.RuleKey;

public class SolidityIssue {

  public static final String REPO_KEY = "solidity-solidity";

  public final RuleKey ruleKey;
  public final Token start;
  public final Token stop;
  public final String message;

  public SolidityIssue(Token from, Token to, String reportMessage, String externalRuleKey) {
    ruleKey = RuleKey.of(REPO_KEY, externalRuleKey);
    message = reportMessage;
    start = from;
    stop = to;
  }
}
