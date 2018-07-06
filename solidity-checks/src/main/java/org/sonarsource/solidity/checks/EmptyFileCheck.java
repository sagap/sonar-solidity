package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;

public class EmptyFileCheck extends IssuableVisitor {
  private static final String RULE_KEY = "ExternalRule3";

  @Override
  public ParseTree visitSourceUnit(SourceUnitContext ctx) {
    if (ctx.getChildCount() <= 1) {
      ruleContext().addIssueOnFile("File has 0 lines of code", RULE_KEY);
    }
    return super.visitSourceUnit(ctx);
  }
}
