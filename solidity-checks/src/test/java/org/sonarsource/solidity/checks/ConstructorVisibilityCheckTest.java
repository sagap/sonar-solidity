package org.sonarsource.solidity.checks;

import org.junit.Test;

public class ConstructorVisibilityCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new ConstructorVisibilityCheck(), "src/test/resources/ConstructorVisibilityCheck/test.sol");
  }
}
