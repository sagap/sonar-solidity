package org.sonarsource.solidity.checks;

import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ContractPartContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierInvocationContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierListContext;
import org.sonarsource.solidity.frontend.SolidityParser.ParameterContext;

@Rule(key = AccessRestrictionPatternCheck.RULE_KEY)
public class AccessRestrictionPatternCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule16";

  @Override
  public ParseTree visitContractPart(ContractPartContext ctx) {
    FunctionDefinitionContext functionDefinition = ctx.functionDefinition();
    if (functionDefinition != null && functionDefinition.block() != null) {
      ModifierListContext modifierList = functionDefinition.modifierList();
      if (modifierList != null && CheckUtils.isPublicOrExternalFunction(modifierList)
        && CheckUtils.isPayableFunction(modifierList.stateMutability())) {
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

}
