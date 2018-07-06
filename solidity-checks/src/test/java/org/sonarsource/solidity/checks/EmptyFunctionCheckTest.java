package org.sonarsource.solidity.checks;

import org.junit.Test;

public class EmptyFunctionCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new EmptyFunctionCheck(), "src/test/resources/EmptyFunctionCheck/test.sol");
  }
}
