package org.sonarsource.solidity;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;

public class MyVisitor extends SolidityBaseVisitor<Token> {

  private NewHighlighting highlighting;

  public MyVisitor(NewHighlighting highlighting) {
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
      } else if (token.getType() == 115) {
        this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(),
          token.getLine(), (token.getCharPositionInLine() + token.getStopIndex() - token.getStartIndex() + 1), TypeOfText.STRING);
      } else if (token.getType() >= 96 && token.getType() <= 100) {
        this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(),
          token.getLine(), (token.getCharPositionInLine() + token.getStopIndex() - token.getStartIndex() + 1), TypeOfText.CONSTANT);
      } else {
      }
    }
  }
}
