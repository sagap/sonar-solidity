package org.sonarsource.solidity.checks;

import org.junit.Test;

public class EmptyFileCheckTest {

  private static final String REPORT_MESSAGE = "The file has 0 lines of code";

  @Test
  public void test() {
    FileRuleVerifier.verifyIssue(new EmptyFileCheck(), "src/test/resources/EmptyFileCheck/test.sol", REPORT_MESSAGE);
  }

  @Test
  public void test2() {
    FileRuleVerifier.verifyNoIssue(new EmptyFileCheck(), "src/test/resources/EmptyFileCheck/test2.sol");
  }
}
