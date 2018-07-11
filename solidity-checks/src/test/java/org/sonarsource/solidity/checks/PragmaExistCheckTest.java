package org.sonarsource.solidity.checks;

import org.junit.Test;

public class PragmaExistCheckTest {

  @Test
  public void test() {
    FileRuleVerifier.verifyNoIssue(new PragmaExistCheck(), "src/test/resources/PragmaExistCheck/test.sol");
  }

  @Test
  public void test1() {
    FileRuleVerifier.verifyIssue(new PragmaExistCheck(), "src/test/resources/PragmaExistCheck/test1.sol", "Specify version pragma.");
  }
}
