package org.sonarsource.solidity.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;

public final class Utils {

  private Utils() {
  }

  private static final Logger LOG = Loggers.get(Utils.class);

  private static final Pattern COMMENT_PATTERN = Pattern.compile(".*\\b+.*");

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
    // parser.emptyLines = Utils.emptyLines(newFile);
    return parser;
  }

  public static SolidityParser returnParserFromParsedFile(CharStream cs) {
    SolidityLexer sl = new SolidityLexer(cs);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokenStream);
    tokenStream.fill();
    parser.addCommentsToSet(tokenStream.getTokens());
    // parser.emptyLines = Utils.emptyLines(newFile);
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

  public static int emptyLines(InputFile file) {
    String line;
    int emptyLinesCounter = 0;
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(file.inputStream()));
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() == 0) {
          emptyLinesCounter++;
        }
      }
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }
    // +1 for the last line
    return emptyLinesCounter;
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
