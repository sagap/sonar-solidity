package org.sonarsource.solidity.checks;

import java.util.Optional;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;

@Rule(key = AvoidTxOriginCheck.RULE_KEY)
public class AvoidTxOriginCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule14";
  private static final String REPORT_MESSAGE = "Avoid usage of \"tx,origin\", in authorization cases"
    + " it is a bottleneck and it will not be compatible in the future.";

  @Override
  public ParseTree visitExpression(ExpressionContext ctx) {
    returnTxOriginFromExpression(ctx).ifPresent(expr -> {
      ruleContext().addIssue(expr, REPORT_MESSAGE, RULE_KEY);
    });
    return super.visitExpression(ctx);
  }

  private static Optional<ExpressionContext> returnTxOriginFromExpression(ExpressionContext stmt) {
    ExpressionContext expr = null;
    if ("tx.origin".equals(stmt.getText())) {
      expr = stmt;
    }
    return Optional.ofNullable(expr);
  }
}
