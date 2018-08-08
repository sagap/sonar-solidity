package org.sonarsource.solidity.frontend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.Token;

public final class Utils {

  private Utils() {
  }

  private static final Pattern COMMENT_PATTERN = Pattern.compile(".*\\b+.*");

  public static String trimQuotes(String value) {
    return value.substring(1, value.length() - 1);
  }

  public static boolean isCommentSignificant(Token token) {
    Matcher matcher = COMMENT_PATTERN.matcher(token.getText());
    return matcher.find();
  }

  public static boolean isCommentSignificant(String token) {
    Matcher matcher = COMMENT_PATTERN.matcher(token);
    return matcher.find();
  }

  public static boolean typeMatches(Token token, int... types) {
    int tokenType = token.getType();
    for (int typeIter : types) {
      if (tokenType == typeIter) {
        return true;
      }
    }
    return false;
  }
}
