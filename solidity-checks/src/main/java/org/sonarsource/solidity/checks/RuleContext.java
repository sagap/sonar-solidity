package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.Token;

public interface RuleContext {

  public void addIssue(Token start, Token stop, String reportMessage, String externalRuleKey);

}
