package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;

@Rule(key = EmptyFileCheck.RULE_KEY)
public class EmptyFileCheck extends IssuableVisitor {
  public static final String RULE_KEY = "ExternalRule3";

  @Override
  public ParseTree visitSourceUnit(SourceUnitContext ctx) {
    if (ctx.getChildCount() <= 1) {
      ruleContext().addIssueOnFile("The file has 0 lines of code", RULE_KEY);
    }
    return super.visitSourceUnit(ctx);
  }
}
