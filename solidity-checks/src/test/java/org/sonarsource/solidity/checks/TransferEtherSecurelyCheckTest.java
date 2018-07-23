package org.sonarsource.solidity.checks;

import org.junit.Test;

public class TransferEtherSecurelyCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new TransferEtherSecurelyCheck(), "src/test/resources/TransferEtherSecurelyCheck/test.sol");
  }
}
