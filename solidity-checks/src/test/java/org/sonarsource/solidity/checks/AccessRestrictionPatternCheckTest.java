package org.sonarsource.solidity.checks;

import org.junit.Test;

public class AccessRestrictionPatternCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new AccessRestrictionPatternCheck(), "src/test/resources/AccessRestrictionPatternCheck/test.sol");
  }
}
