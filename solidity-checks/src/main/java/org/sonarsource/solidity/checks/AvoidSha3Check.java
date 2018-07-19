package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;

@Rule(key = AvoidSha3Check.RULE_KEY)
public class AvoidSha3Check extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule15";

  @Override
  public ParseTree visitFunctionCall(FunctionCallContext ctx) {
    CheckUtils.extractNameFromFunction(ctx).ifPresent(functionName -> {
      if ("sha3".equals(functionName)) {
        ruleContext().addIssue(ctx.identifier(0), "\"sha3\" is deprecated, use \"keccak256\" instead.", RULE_KEY);
      }
    });
    return super.visitFunctionCall(ctx);
  }
}
