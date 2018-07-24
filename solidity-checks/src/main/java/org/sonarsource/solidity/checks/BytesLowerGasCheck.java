package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.StateVariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.TypeNameContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationContext;

@Rule(key = BytesLowerGasCheck.RULE_KEY)
public class BytesLowerGasCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule23";

  @Override
  public ParseTree visitStateVariableDeclaration(StateVariableDeclarationContext ctx) {
    checkToReport(ctx.typeName());
    return super.visitStateVariableDeclaration(ctx);
  }

  @Override
  public ParseTree visitVariableDeclaration(VariableDeclarationContext ctx) {
    checkToReport(ctx.typeName());
    return super.visitVariableDeclaration(ctx);
  }

  private void checkToReport(TypeNameContext typeVariable) {
    if (typeVariable != null && variableTypeIsBytes(typeVariable)) {
      ruleContext().addIssue(typeVariable, "\"bytes\" should be used instead of \"byte[]\","
        + " due to lower gas consumption.", RULE_KEY);
    }
  }

  private static boolean variableTypeIsBytes(TypeNameContext varType) {
    return "byte[]".equals(varType.getText());
  }
}
