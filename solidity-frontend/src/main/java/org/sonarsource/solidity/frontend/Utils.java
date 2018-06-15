package org.sonarsource.solidity.frontend;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

public final class Utils {

  private Utils() {
  }

  public static String trimQuotes(String value) {
    return value.substring(1, value.length() - 1);
  }

  // ... parser
  public static SolidityParser returnParserUnitFromParsedFile(String file) {
    CharStream cs = CharStreams.fromString(file);
    SolidityLexer sl = new SolidityLexer(cs);
    // TokenStream tokens = new CommonTokenStream(sl);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokenStream);
    tokenStream.fill();
    parser.handleComments(tokenStream.getTokens());
    return parser;
  }

  public static SolidityParser returnParserFromParsedFile(CharStream cs) {
    SolidityLexer sl = new SolidityLexer(cs);
    // TokenStream tokens = new CommonTokenStream(sl);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokenStream);
    tokenStream.fill();
    parser.handleComments(tokenStream.getTokens());
    return parser;
  }

  public static void handleStructuredComments(Token token) {
    // System.out.println(token);
    char[] temp = token.getText().toCharArray();
    int counter = 0;
    // int line = token.getText().ge
    // token.getText().lastIndexOf('\n');

  }
  // if (token.getText().contains("\n"))
  // System.out.println("AAAAAAAAAAA");
}
