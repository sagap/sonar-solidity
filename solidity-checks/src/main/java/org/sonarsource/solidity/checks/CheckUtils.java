package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ReturnStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.SimpleStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationStatementContext;

public class CheckUtils {

  private CheckUtils() {
  }

  private static final List<String> COMPARING_OPERATORS = ImmutableList.<String>builder()
    .add("==")
    .add("!=")
    .add("<")
    .add(">")
    .add("<=")
    .add(">=")
    .build();

  public static String returnContentOfComments(String comment) {
    int idx = comment.indexOf('{');
    return comment.substring(idx + 2, comment.length() - 2).trim();

  }

  public static boolean isCommentForReporting(String comment) {
    return comment.startsWith("// Noncompliant {{") && comment.endsWith("}}");
  }

  public static boolean isParenthesized(ParseTree expr) {
    if (ExpressionContext.class.isInstance(expr)) {
      String expression = expr.getText();
      return expression.startsWith("(") && expression.endsWith(")");
    }
    return false;
  }

  public static ParseTree removeParenthesis(ParseTree tree) {
    return tree.getChild(1);
  }

  public static boolean isBooleanExpression(ParseTree tree) {
    ExpressionContext expr = (ExpressionContext) tree;
    ParseTree expression = expr.getChild(1);
    return expression != null && COMPARING_OPERATORS.contains(expression.getText());
  }

  public static boolean treeMatches(ParseTree tree, Class context) {
    return context.isInstance(tree);
  }

  public static Optional<ParseTree> checkForElseStatement(ParserRuleContext ctxNode) {
    if (!ctxNode.children.isEmpty() && (((IfStatementContext) ctxNode).Else()) != null) {
      ParseTree childNode = ctxNode.children.get(6);
      // exclude else - if cases
      if (childNode.getChildCount() > 0 && !childNode.getChild(0).getClass().equals(IfStatementContext.class))
        return Optional.of(childNode);
    }
    return Optional.empty();
  }

  public static ParseTree findContractParentNode(ParseTree tree) {
    tree = tree.getParent();
    while (!treeMatches(tree, ContractDefinitionContext.class)) {
      tree = tree.getParent();
    }
    return tree;
  }

  public static boolean isElseIfStatement(IfStatementContext ctx) {
    return ctx.getParent().getRuleIndex() == 38 && ctx.getParent().getParent().getRuleIndex() == 40;
  }

  public static boolean isTernaryExpression(StatementContext ctx) {
    String statementName = ctx.getChild(0).getClass().getSimpleName();
    ExpressionContext expr = null;
    switch (statementName) {
      case "ReturnStatementContext":
        ReturnStatementContext retStmt = ctx.returnStatement();
        expr = retStmt.expression();
        return expr != null && (expr.getToken(SolidityParser.T__71, 0) != null);
      case "SimpleStatementContext":
        SimpleStatementContext simpleStmt = ctx.simpleStatement();
        VariableDeclarationStatementContext varDeclStmt = simpleStmt.variableDeclarationStatement();
        if (varDeclStmt != null) {
          expr = varDeclStmt.expression();
          return expr != null && (expr.getToken(SolidityParser.T__71, 0) != null);
        }
        return false;
      default:
    }
    return false;
  }

  public static TerminalNode getOpenCurlyBrace(ParserRuleContext ctx) {
    return ctx.getTokens(SolidityParser.T__13).get(0);
  }

  public static Optional<String> extractNameFromFunction(ParserRuleContext functionContext) {
    IdentifierContext functionIdentifier = null;
    String functionName = null;
    if (CheckUtils.treeMatches(functionContext, FunctionDefinitionContext.class)) {
      functionIdentifier = ((FunctionDefinitionContext) functionContext).identifier();
    } else if (CheckUtils.treeMatches(functionContext, FunctionCallContext.class)) {
      functionIdentifier = ((FunctionCallContext) functionContext).identifier(0);
    }
    if (functionIdentifier != null) {
      functionName = functionIdentifier.getText();
    }
    return Optional.ofNullable(functionName);
  }
}
