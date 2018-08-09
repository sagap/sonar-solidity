package org.sonarsource.solidity.frontend;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;

public class SolidityParsingPhase {

  // important to maintain order of comments for reporting
  public final Set<Token> comments;
  private Integer emptyLines;
  private Integer linesOfComments;
  private TokenStream tokens;

  public SolidityParsingPhase() {
    linesOfComments = 0;
    emptyLines = 0;
    comments = new LinkedHashSet<>();
  }

  public SourceUnitContext parse(String fileContents) {
    CharStream cs = CharStreams.fromString(fileContents);
    SolidityLexer sl = new SolidityLexer(cs);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    tokenStream.fill();
    SolidityParser parser = new SolidityParser(tokenStream);
    parser.setErrorHandler(new ErrorStrategy());
    addCommentsToSet(tokenStream.getTokens());
    this.tokens = parser.getTokenStream();
    return parser.sourceUnit();
  }

  public SourceUnitContext parse(CharStream cs) {
    SolidityLexer sl = new SolidityLexer(cs);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    tokenStream.fill();
    SolidityParser parser = new SolidityParser(tokenStream);
    parser.setErrorHandler(new ErrorStrategy());
    addCommentsToSet(tokenStream.getTokens());
    this.tokens = parser.getTokenStream();
    return parser.sourceUnit();
  }

  public void addCommentsToSet(List<? extends Token> tokens) {
    for (Token token : tokens) {
      if (token.getChannel() == 1) {
        comments.add(token);
        if (Utils.isCommentSignificant(token)) {
          linesOfComments++;
        }
      }
    }
  }

  public Integer getEmptyLines() {
    return emptyLines;
  }

  public void setEmptyLines(Integer emptyLines) {
    this.emptyLines = emptyLines;
  }

  public Integer getLinesOfComments() {
    return linesOfComments;
  }

  public TokenStream getTokens() {
    return tokens;
  }
}
