package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;

public class IssuableVisitor extends SolidityBaseVisitor<ParseTree> {

  private RuleContext ruleContext;

  public void setRuleContext(RuleContext ruleContext) {
    this.ruleContext = ruleContext;
  }

  public final RuleContext ruleContext() {
    return ruleContext;
  }
}
