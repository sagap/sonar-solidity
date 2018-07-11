package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;

@Rule(key = PragmaExistCheck.RULE_KEY)
public class PragmaExistCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule12";

  @Override
  public ParseTree visitContractDefinition(ContractDefinitionContext ctx) {
    SourceUnitContext suc = findSourceContextUnit(ctx);
    if (suc.pragmaDirective().isEmpty()) {
      ruleContext().addIssueOnFile("Specify version pragma.", RULE_KEY);
    }
    return super.visitContractDefinition(ctx);

  }

  private static SourceUnitContext findSourceContextUnit(ContractDefinitionContext ctx) {
    ParseTree temp = ctx.getParent();
    /*
     * while (!SourceUnitContext.class.isInstance(temp)) {
     * temp = temp.getParent();
     * }
     */
    return (SourceUnitContext) temp;
  }
}
