package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationStatementContext;

@Rule(key = DeprecatedVarCheck.RULE_KEY)
public class DeprecatedVarCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule25";
  private static final String VAR_KEYWORD = "var";

  @Override
  public ParseTree visitVariableDeclarationStatement(VariableDeclarationStatementContext ctx) {
    VariableDeclarationContext variableDeclaration = ctx.variableDeclaration();
    if (variableDeclaration != null && typeIsVar(variableDeclaration.typeName())
      || isTupleAssignment(ctx)) {
      ruleContext().addIssue(ctx.getStart(), ctx.getStop(), "\"var\" keyword is deprecated.", RULE_KEY);
    }
    return super.visitVariableDeclarationStatement(ctx);
  }

  private static boolean typeIsVar(ParseTree varType) {
    return VAR_KEYWORD.equals(varType.getText());
  }

  private static boolean isTupleAssignment(VariableDeclarationStatementContext variableDeclaration) {
    return VAR_KEYWORD.equals(variableDeclaration.getChild(0).getText());
  }
}
