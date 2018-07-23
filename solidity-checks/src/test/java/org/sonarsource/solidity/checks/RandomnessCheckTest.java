package org.sonarsource.solidity.checks;

import org.junit.Test;

public class RandomnessCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new RandomnessCheck(), "src/test/resources/RandomnessCheck/test.sol");
  }
}
