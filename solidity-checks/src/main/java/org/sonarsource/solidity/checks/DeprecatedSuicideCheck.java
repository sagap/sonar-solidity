package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallArgumentsContext;

@Rule(key = DeprecatedSuicideCheck.RULE_KEY)
public class DeprecatedSuicideCheck extends IssuableVisitor {
  public static final String RULE_KEY = "ExternalRule13";

  @Override
  public ParseTree visitFunctionCallArguments(FunctionCallArgumentsContext ctx) {
    ExpressionContext functionCall = (ExpressionContext) ctx.getParent();
    if ("suicide".equals(functionCall.expression(0).getText())) {
      ruleContext().addIssue(functionCall.getStart(), functionCall.getStop(), "\"Selfdestruct\" should be used instead of the deprecated \"suicide\".",
        RULE_KEY);
    }
    return super.visitFunctionCallArguments(ctx);
  }
}
