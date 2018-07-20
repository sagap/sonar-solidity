package org.sonarsource.solidity.checks;

import java.util.List;
import java.util.Objects;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ContractPartContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierInvocationContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierListContext;
import org.sonarsource.solidity.frontend.SolidityParser.ParameterContext;
import org.sonarsource.solidity.frontend.SolidityParser.StateMutabilityContext;

@Rule(key = AccessRestrictionPatternCheck.RULE_KEY)
public class AccessRestrictionPatternCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule16";

  @Override
  public ParseTree visitContractPart(ContractPartContext ctx) {
    FunctionDefinitionContext functionDefinition = ctx.functionDefinition();
    if (functionDefinition != null && functionDefinition.block() != null) {
      ModifierListContext modifierList = functionDefinition.modifierList();
      if (modifierList != null && isPublicOrExternalFunction(modifierList) && isPayableFunction(modifierList.stateMutability())) {
        List<ModifierInvocationContext> modifierInvocationList = modifierList.modifierInvocation();
        List<ParameterContext> parameterList = functionDefinition.parameterList().parameter();
        if (modifierInvocationList.isEmpty() && !parameterList.isEmpty()) {
          ruleContext().addIssue(functionDefinition.getStart(), modifierList.getStop(),
            modifierList.getStop().getText().length(),
            "You should restrict access using modifiers, apply Access Restriction Pattern.",
            RULE_KEY);
        }
      }
    }
    return super.visitContractPart(ctx);
  }

  private static boolean isPayableFunction(List<StateMutabilityContext> stateMutabilityListCtx) {
    return stateMutabilityListCtx.stream()
      .map(StateMutabilityContext::PayableKeyword)
      .filter(Objects::nonNull)
      .count() == 1;
  }

  private static boolean isPublicOrExternalFunction(ModifierListContext modifierList) {
    return noVisibilitySpecified(modifierList)
      || modifierList.PublicKeyword(0) != null || modifierList.ExternalKeyword(0) != null;
  }

  private static boolean noVisibilitySpecified(ModifierListContext modifierList) {
    return modifierList.InternalKeyword(0) == null && modifierList.ExternalKeyword(0) == null
      && modifierList.PublicKeyword(0) == null && modifierList.PrivateKeyword(0) == null;
  }
}
