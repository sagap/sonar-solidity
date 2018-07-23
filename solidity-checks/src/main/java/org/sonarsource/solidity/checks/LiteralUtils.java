package org.sonarsource.solidity.checks;

public class LiteralUtils {

  private LiteralUtils() {
  }

  public static String removeQuotesFromStringLiteral(String literal) {
    return literal.substring(1, literal.length() - 1);
  }
}
