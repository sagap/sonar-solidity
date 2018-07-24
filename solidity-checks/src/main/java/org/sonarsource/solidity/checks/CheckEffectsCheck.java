package org.sonarsource.solidity.checks;

import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.BlockContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.SimpleStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;

@Rule(key = CheckEffectsCheck.RULE_KEY)
public class CheckEffectsCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule22";

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    if (CheckUtils.isPublicOrExternalFunction(ctx.modifierList())) {
      BlockContext functionBlock = ctx.block();
      if (functionBlock != null) {
        List<StatementContext> stmt = functionBlock.statement();
        if (!stmt.isEmpty() && !statementIsRequireFunctionCall(stmt.get(0)) && ctx.identifier() != null) {
          ruleContext().addIssue(ctx.identifier(), "Checks should be done at the beggining of "
            + "any function making external calls.", RULE_KEY);
        }
      }
    }
    return super.visitFunctionDefinition(ctx);
  }

  private static boolean statementIsRequireFunctionCall(StatementContext stmt) {
    SimpleStatementContext simpleStmt = stmt.simpleStatement();
    if (simpleStmt != null) {
      ExpressionStatementContext expressionStatement = simpleStmt.expressionStatement();
      if (expressionStatement != null) {
        FunctionCallContext functionCall = expressionStatement.expression().functionCall();
        if (functionCall != null
          && "require".equals(functionCall.identifier(0).getText())) {
          return true;
        }
      }
    }
    return false;
  }
}
