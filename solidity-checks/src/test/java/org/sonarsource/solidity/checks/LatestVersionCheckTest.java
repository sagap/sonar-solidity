package org.sonarsource.solidity.checks;

import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.Test;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.Utils;

public class LatestVersionCheckTest {

  @Test
  public void test() {
    CharStream cs;
    try {
      cs = CharStreams.fromFileName("src/resources/LatestVersionCheck/test.sol");
      SolidityParser parser = Utils.returnParserFromParsedFile(cs);
      LatestVersionCheck check = new LatestVersionCheck();
      check.visit(parser.sourceUnit());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
