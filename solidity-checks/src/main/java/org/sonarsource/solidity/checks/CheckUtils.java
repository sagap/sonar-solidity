package org.sonarsource.solidity.checks;

public class CheckUtils {

  private CheckUtils() {
  }

  public static String returnContentOfComments(String comment) {
    int idx = comment.indexOf('{');
    return comment.substring(idx + 2, comment.length() - 2).trim();

  }

  public static boolean isCommentForReporting(String comment) {
    return comment.startsWith("// Noncompliant {{") && comment.endsWith("}}");
  }
}
