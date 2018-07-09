package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ConstructorDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierListContext;

@Rule(key = ConstructorVisibilityCheck.RULE_KEY)
public class ConstructorVisibilityCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule7";

  @Override
  public ParseTree visitConstructorDefinition(ConstructorDefinitionContext ctx) {
    if (modifierListIsEmpty(ctx.modifierList())) {
      report(ctx.getStart(), ctx.block().getStart());
    }
    return super.visitConstructorDefinition(ctx);
  }

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    if (isConstructor(ctx) && modifierListIsEmpty(ctx.modifierList())) {
      report(ctx.getStart(), ctx.block().getStart());
    }
    return super.visitFunctionDefinition(ctx);
  }

  private static boolean modifierListIsEmpty(ModifierListContext modifierList) {
    return modifierList.getChildCount() == 0;
  }

  private void report(Token from, Token to) {
    ruleContext().addIssue(from, to, "Specify constructor's visibility.", RULE_KEY);
  }

  private static boolean isConstructor(FunctionDefinitionContext function) {
    ParseTree parent = CheckUtils.findContractParentNode(function);
    return function.identifier().getText().equals(((ContractDefinitionContext) parent).identifier().getText());
  }
}
