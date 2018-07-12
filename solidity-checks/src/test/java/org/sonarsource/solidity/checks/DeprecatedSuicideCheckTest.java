package org.sonarsource.solidity.checks;

import org.junit.Test;

public class DeprecatedSuicideCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new DeprecatedSuicideCheck(), "src/test/resources/DeprecatedSuicideCheck/test.sol");
  }
}
