package org.sonarsource.solidity.checks;

import org.junit.Test;

public class TightVariablePackingCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new TightVariablePackingCheck(), "src/test/resources/TightVariablePackingCheck/test.sol");
  }
}
