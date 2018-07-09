package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;

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

  public static ParseTree findContractParentNode(ParseTree tree) {
    tree = tree.getParent();
    while (!treeMatches(tree, ContractDefinitionContext.class)) {
      tree = tree.getParent();
    }
    return tree;
  }
}
