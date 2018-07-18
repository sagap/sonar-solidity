package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;

@Rule(key = DeprecatedSuicideCheck.RULE_KEY)
public class DeprecatedSuicideCheck extends IssuableVisitor {
  public static final String RULE_KEY = "ExternalRule13";

  @Override
  public ParseTree visitFunctionCall(FunctionCallContext ctx) {
    CheckUtils.extractNameFromFunction(ctx).ifPresent(functionName -> {
      if ("suicide".equals(functionName)) {
        ruleContext().addIssue(ctx.getStart(), ctx.getStop(), "\"Selfdestruct\" should be used"
          + " instead of the deprecated \"suicide\".", RULE_KEY);
      }
    });
    return super.visitFunctionCall(ctx);
  }
}
