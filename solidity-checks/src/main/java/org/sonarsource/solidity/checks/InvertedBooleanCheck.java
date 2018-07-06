package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;

@Rule(key = InvertedBooleanCheck.RULE_KEY)
public class InvertedBooleanCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule5";

  @Override
  public ParseTree visitExpression(ExpressionContext ctx) {
    ParseTree expr = ctx.getChild(0);
    if (expr.getText().startsWith("!")) {
      ruleContext().addIssue(ctx.getStart(), ctx.getStop(), "Boolean expression should not be inverted.", RULE_KEY);
    }
    return super.visitExpression(ctx);
  }
}
