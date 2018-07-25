package org.sonarsource.solidity.checks;

import org.junit.Test;

public class DeprecatedVarCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new DeprecatedVarCheck(), "src/test/resources/DeprecatedVarCheck/test.sol");
  }
}
