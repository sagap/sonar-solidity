package org.sonarsource.solidity.checks;

import org.junit.Test;

public class DeprecatedConstructorCheckTest {
  @Test
  public void test() {
    new CheckVerifier(new DeprecatedConstructorCheck(), "src/test/resources/DeprecatedConstructorCheck/test.sol");
  }
}
