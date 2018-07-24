package org.sonarsource.solidity.checks;

import org.junit.Test;

public class CheckEffectsCheckTest {
  @Test
  public void test() {
    new CheckVerifier(new CheckEffectsCheck(), "src/test/resources/CheckEffectsCheck/test.sol");
  }
}
