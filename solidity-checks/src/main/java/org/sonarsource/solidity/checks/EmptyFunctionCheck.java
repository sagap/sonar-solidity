package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;

@Rule(key = EmptyFunctionCheck.RULE_KEY)
public class EmptyFunctionCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule4";

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    if (ctx.block() != null && ctx.block().statement().isEmpty())
      ruleContext().addIssue(ctx.getStart(), ctx.getStop(), "Function should not be empty.", RULE_KEY);
    return super.visitFunctionDefinition(ctx);
  }
}
