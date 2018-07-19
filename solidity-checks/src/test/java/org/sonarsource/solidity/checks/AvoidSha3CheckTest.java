package org.sonarsource.solidity.checks;

import org.junit.Test;

public class AvoidSha3CheckTest {

  @Test
  public void test() {
    new CheckVerifier(new AvoidSha3Check(), "src/test/resources/AvoidSha3Check/test.sol");
  }
}
