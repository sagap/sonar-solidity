package org.sonarsource.solidity.frontend;

public final class Utils {

  private Utils() {
  }

  public static String trimQuotes(String value) {
    return value.substring(1, value.length() - 1);
  }
}
