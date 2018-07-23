package org.sonarsource.solidity.checks;

import org.junit.Test;

public class GuardCheckPatternCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new GuardCheckPatternCheck(), "src/test/resources/GuardCheckPatternCheck/test.sol");
  }
}
