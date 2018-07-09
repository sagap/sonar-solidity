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
    if (ctx.primaryExpression() == null && expr.getText().startsWith("!")) {
      if (CheckUtils.isParenthesized(ctx.getChild(1)) && CheckUtils.isBooleanExpression(CheckUtils.removeParenthesis(ctx.getChild(1)))) {
        ruleContext().addIssue(ctx.getStart(), ctx.getStop(), "Boolean expression should not be inverted.", RULE_KEY);
      }
    }
    return super.visitExpression(ctx);
  }
}
