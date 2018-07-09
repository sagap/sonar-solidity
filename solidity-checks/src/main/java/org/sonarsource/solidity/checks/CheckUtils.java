package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;

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
    //
    ParseTree expression = expr.getChild(1);
    return expression != null && COMPARING_OPERATORS.contains(expression.getText());
  }
}
