package org.sonarsource.solidity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ReturnStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.SimpleStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationStatementContext;

public final class UtilsSensor {

  private static final Logger LOG = Loggers.get(UtilsSensor.class);

  public static Optional<ParseTree> checkForElseStatement(ParserRuleContext ctxNode) {
    if (!ctxNode.children.isEmpty() && ctxNode.children.size() >= 6) {
      // the 6th child is where else exists even for else-if case
      Token token = (Token) ctxNode.children.get(5).getPayload();
      if (token.getType() == 40) {
        ParseTree child6 = ctxNode.children.get(6);
        // exclude else - if cases
        if (!child6.getChild(0).getClass().equals(IfStatementContext.class))
          return Optional.of(child6);
      }
    }
    return Optional.empty();
  }

  public static boolean isElseIfStatement(IfStatementContext ctx) {
    return ctx.getParent().getRuleIndex() == 38 && ctx.getParent().getParent().getRuleIndex() == 40;
  }

  public static int emptyLines(InputFile file) {
    String line;
    int emptyLinesCounter = 0;
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(file.inputStream()));
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() == 0) {
          emptyLinesCounter++;
        }
      }
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }
    // +1 for the last line
    return emptyLinesCounter;
  }

  public static boolean isTernaryExpression(StatementContext ctx) {
    String statementName = ctx.getChild(0).getClass().getSimpleName();
    ExpressionContext expr = null;
    switch (statementName) {
      case "ReturnStatementContext":
        ReturnStatementContext retStmt = ctx.returnStatement();
        expr = retStmt.expression();
        return (expr.getToken(SolidityParser.TERNARY_OPERATOR, 0) != null);
      case "SimpleStatementContext":
        SimpleStatementContext simpleStmt = ctx.simpleStatement();
        VariableDeclarationStatementContext varDeclStmt = simpleStmt.variableDeclarationStatement();
        if (varDeclStmt != null) {
          expr = varDeclStmt.expression();
          return (expr.getToken(SolidityParser.TERNARY_OPERATOR, 0) != null);
        }
      default:
    }
    return false;
  }

  /*
   * public static ExpressionContext countTernaryExpressionOperators(StatementContext ctx) {
   * String statementName = ctx.getChild(0).getClass().getSimpleName();
   * switch (statementName) {
   * case "ReturnStatementContext":
   * ReturnStatementContext retStmt = ctx.returnStatement();
   * ExpressionContext expr = retStmt.expression().expression(0);
   * return expr;
   * case "SimpleStatementContext":
   * VariableDeclarationStatementContext varDeclStmt = ctx.simpleStatement().variableDeclarationStatement();
   * if (varDeclStmt != null) {
   * expr = varDeclStmt.expression();
   * return expr;
   * }
   * }
   * return null;
   * }
   */
}
