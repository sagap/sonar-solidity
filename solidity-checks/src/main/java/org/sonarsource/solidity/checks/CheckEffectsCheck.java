package org.sonarsource.solidity.checks;

import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.BlockContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;
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
        statementIsTransferOrSend(functionBlock.statement())
          .ifPresent(stmt -> ruleContext().addIssue(stmt, "External call should always be last statement in a function.", RULE_KEY));
      }
    }
    return super.visitFunctionDefinition(ctx);
  }

  private static Optional<SimpleStatementContext> statementIsTransferOrSend(List<StatementContext> statementsList) {
    int statementsListSize = statementsList.size();
    for (int i = 0; i < statementsListSize; i++) {
      StatementContext stmt = statementsList.get(i);
      if (SimpleStatementContext.class.isInstance(stmt.getChild(0))) {
        SimpleStatementContext simpleStmt = stmt.simpleStatement();
        ExpressionStatementContext expressionStatement = simpleStmt.expressionStatement();
        if (expressionStatement != null) {
          FunctionCallContext functionCall = expressionStatement.expression().functionCall();
          if (functionCall != null && i < statementsListSize - 1 &&
            functionCallIdentifierIsTransferOrSend(functionCall.identifier())) {
            return Optional.of(simpleStmt);
          }
        }
      }
    }
    return Optional.empty();
  }

  private static boolean functionCallIdentifierIsTransferOrSend(List<IdentifierContext> functionCallIdentifier) {
    for (IdentifierContext identifier : functionCallIdentifier) {
      String identifierText = identifier.getText();
      if ("transfer".equals(identifierText) || "send".equals(identifierText)) {
        return true;
      }
    }
    return false;
  }
}
