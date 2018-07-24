package org.sonarsource.solidity.checks;

import org.junit.Test;

public class BytesLowerGasCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new BytesLowerGasCheck(), "src/test/resources/BytesLowerGasCheck/test.sol");
  }
}
