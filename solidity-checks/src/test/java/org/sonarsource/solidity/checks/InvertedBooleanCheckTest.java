package org.sonarsource.solidity.checks;

import org.junit.Test;

public class InvertedBooleanCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new InvertedBooleanCheck(), "src/test/resources/InvertedBooleanCheck/test.sol");
  }
}
