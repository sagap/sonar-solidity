package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;

@Rule(key = ContractNotEmptyCheck.RULE_KEY)
public class ContractNotEmptyCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule2";

  @Override
  public ParseTree visitContractDefinition(ContractDefinitionContext ctx) {
    if (ctx.contractPart().isEmpty())
      ruleContext().addIssue(ctx.getStart(), ctx.identifier().getStart(), "Contract should not be empty", RULE_KEY);
    return super.visitContractDefinition(ctx);
  }
}
