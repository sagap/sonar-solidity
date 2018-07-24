package org.sonarsource.solidity;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;
import org.sonarsource.analyzer.commons.TokenLocation;
import org.sonarsource.solidity.frontend.SolidityLexer;

public class SolidityCpd {

  private SolidityCpd() {
  }

  public static void addTokensCpd(NewCpdTokens cpdTokens, String fileContents) {
    CharStream cs = CharStreams.fromString(fileContents);
    SolidityLexer sl = new SolidityLexer(cs);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    tokenStream.fill();
    for (Token token : tokenStream.getTokens()) {
      // Type -1, represents EOF
      if (token.getType() != -1) {
        TokenLocation tokenLocation = new TokenLocation(token.getLine(), token.getCharPositionInLine(), token.getText());
        cpdTokens.addToken(tokenLocation.startLine(), tokenLocation.startLineOffset(),
          tokenLocation.endLine(), tokenLocation.endLineOffset(), token.getText());
      }
    }
    cpdTokens.save();
  }
}
