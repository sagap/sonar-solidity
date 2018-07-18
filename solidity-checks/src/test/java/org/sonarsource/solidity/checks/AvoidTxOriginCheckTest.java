package org.sonarsource.solidity.checks;

import org.junit.Test;

public class AvoidTxOriginCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new AvoidTxOriginCheck(), "src/test/resources/AvoidTxOriginCheck/test.sol");
  }
}
