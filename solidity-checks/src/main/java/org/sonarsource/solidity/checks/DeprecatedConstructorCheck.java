package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;

@Rule(key = DeprecatedConstructorCheck.RULE_KEY)
public class DeprecatedConstructorCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule8";

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    ContractDefinitionContext contractParent = (ContractDefinitionContext) CheckUtils.findContractParentNode(ctx);
    if (ctx.identifier() != null && contractParent.identifier().getText().equals(ctx.identifier().getText())) {
      ruleContext().addIssue(ctx.getStart(), ctx.block().getStart(),
        "Defining constructor wuth the name of the contract is deprecated use constructor(...){...}", RULE_KEY);
    }
    return super.visitFunctionDefinition(ctx);
  }
}
