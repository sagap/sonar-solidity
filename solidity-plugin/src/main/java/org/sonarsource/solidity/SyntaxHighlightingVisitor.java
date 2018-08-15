package org.sonarsource.solidity;

import java.util.Set;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.Utils;

public class SyntaxHighlightingVisitor extends SolidityBaseVisitor<Token> {

  private NewHighlighting highlighting;

  public SyntaxHighlightingVisitor(NewHighlighting highlighting) {
    this.highlighting = highlighting;
  }

  public void visitTokens(TokenStream tokenStream) {
    for (int i = 0; i < tokenStream.size(); i++) {
      Token token = tokenStream.get(i);
      if (SoliditySensor.KEYWORDS.contains(token.getText())) {
        this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(),
          token.getLine(), (token.getCharPositionInLine() + token.getStopIndex() - token.getStartIndex() + 1), TypeOfText.KEYWORD);
      } else if (SoliditySensor.KEYWORD_TYPES.contains(token.getText())) {
        this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(),
          token.getLine(), (token.getCharPositionInLine() + token.getStopIndex() - token.getStartIndex() + 1), TypeOfText.KEYWORD_LIGHT);
      } else if (Utils.typeMatches(token, SolidityParser.StringLiteral)) {
        this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(),
          token.getLine(), (token.getCharPositionInLine() + token.getStopIndex() - token.getStartIndex() + 1), TypeOfText.STRING);
      } else if (Utils.typeMatches(token, SolidityParser.VersionLiteral, SolidityParser.BooleanLiteral,
        SolidityParser.DecimalNumber, SolidityParser.HexNumber, SolidityParser.NumberUnit, SolidityParser.HexLiteral)) {
        this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(),
          token.getLine(), (token.getCharPositionInLine() + token.getStopIndex() - token.getStartIndex() + 1), TypeOfText.CONSTANT);
      } else {
        // no more cases to add
      }
    }
  }

  public void highlightComments(Set<Token> comments) {
    for (Token token : comments) {
      if (token.getType() == SolidityParser.LINE_COMMENT) {
        highlightComment(token);
      } else {
        handleStructuredComments(token);
      }
    }
  }

  public void handleStructuredComments(Token token) {
    int newLineOccurences = token.getText().split("\r\n|\r|\n").length - 1;
    int lastIndexNewLine = token.getText().lastIndexOf('\n');
    int lastIndex = token.getText().length();
    if (lastIndexNewLine == -1) {
      lastIndexNewLine = 0;
      this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(), (token.getLine() + newLineOccurences), (lastIndex - lastIndexNewLine),
        TypeOfText.STRUCTURED_COMMENT);
    } else {
      this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(), (token.getLine() + newLineOccurences), (lastIndex - lastIndexNewLine - 1),
        TypeOfText.STRUCTURED_COMMENT);
    }
  }

  private void highlightComment(Token token) {
    this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(), token.getLine(), (token.getCharPositionInLine() + token.getText().length()), TypeOfText.COMMENT);
  }
}
