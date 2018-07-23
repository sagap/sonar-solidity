package org.sonarsource.solidity.checks;

import org.junit.Test;

public class OraclePatternCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new OraclePatternCheck(), "src/test/resources/OraclePatternCheck/test.sol");
  }
}
