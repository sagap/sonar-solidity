package org.sonarsource.solidity.frontend;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class ErrorStrategy extends DefaultErrorStrategy {

  private static final Logger LOG = Loggers.get(ErrorStrategy.class);

  @Override
  public void reportError(Parser recognizer, RecognitionException e) {
    Token t = recognizer.getCurrentToken();
    String errorMessage = String.format(
      "Unexpected parsing error occurred. Last found valid token: %s at position %s:%s",
      getTokenErrorDisplay(t),
      t.getLine(),
      t.getCharPositionInLine());
    LOG.debug(errorMessage);
  }

  @Override
  protected void reportMissingToken(Parser recognizer) {
    Token t = recognizer.getCurrentToken();
    IntervalSet expecting = getExpectedTokens(recognizer);
    String errorMessage = String.format(
      "missing %s before %s at position %s:%s",
      expecting.toString(recognizer.getVocabulary()),
      getTokenErrorDisplay(t),
      t.getLine(),
      t.getCharPositionInLine());
    LOG.debug(errorMessage);
  }
}