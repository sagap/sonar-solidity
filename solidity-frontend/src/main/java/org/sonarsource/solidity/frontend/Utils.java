package org.sonarsource.solidity.frontend;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public final class Utils {

  private Utils() {
  }

  public static String trimQuotes(String value) {
    return value.substring(1, value.length() - 1);
  }

  public static SolidityParser returnParserUnitFromParsedFile(String file) {
    CharStream cs = CharStreams.fromString(file);
    SolidityLexer sl = new SolidityLexer(cs);
    // could use this instead: TokenStream tokens = new CommonTokenStream(sl);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokenStream);
    tokenStream.fill();
    parser.addCommentsToSet(tokenStream.getTokens());
    return parser;
  }

  public static SolidityParser returnParserFromParsedFile(CharStream cs) {
    SolidityLexer sl = new SolidityLexer(cs);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokenStream);
    tokenStream.fill();
    parser.addCommentsToSet(tokenStream.getTokens());
    return parser;
  }

}
