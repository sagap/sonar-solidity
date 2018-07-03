package org.sonarsource.solidity.checks;

import org.junit.Test;

public class LatestVersionCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new LatestVersionCheck(), "src/test/resources/LatestVersionCheck/test.sol");
  }

  @Test
  public void test1() {
    CheckVerifier.verifyNoIssue(new LatestVersionCheck(), "src/test/resources/LatestVersionCheck/test1.sol");
  }
}
