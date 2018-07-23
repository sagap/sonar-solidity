package org.sonarsource.solidity.frontend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

public final class Utils {

  private Utils() {
  }

  private static final Pattern COMMENT_PATTERN = Pattern.compile(".*\\b+.*");

  public static String trimQuotes(String value) {
    return value.substring(1, value.length() - 1);
  }

  public static SolidityParser returnParserUnitFromParsedFile(String fileContents) {
    CharStream cs = CharStreams.fromString(fileContents);
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

  public static boolean isCommentSignificant(Token token) {
    Matcher matcher = COMMENT_PATTERN.matcher(token.getText());
    return matcher.find();
  }

  public static boolean isCommentSignificant(String token) {
    Matcher matcher = COMMENT_PATTERN.matcher(token);
    return matcher.find();
  }
}
