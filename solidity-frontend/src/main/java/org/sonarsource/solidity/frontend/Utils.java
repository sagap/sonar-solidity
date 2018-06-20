package org.sonarsource.solidity.frontend;

import java.util.Optional;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;

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

  public static boolean isElseIfStatement(IfStatementContext ctx) {
    return ctx.getParent().getRuleIndex() == 38 && ctx.getParent().getParent().getRuleIndex() == 40;
  }
}
