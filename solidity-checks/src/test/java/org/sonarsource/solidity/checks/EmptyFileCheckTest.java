package org.sonarsource.solidity.checks;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmptyFileCheckTest {

  @Test
  public void test() {
    assertThat(CheckVerifier.verifyIssueOnFile(new EmptyFileCheck(), "src/test/resources/EmptyFileCheck/test.sol")).isTrue();

  }

  @Test
  public void test2() {
    CheckVerifier.verifyNoIssue(new EmptyFileCheck(), "src/test/resources/EmptyFileCheck/test2.sol");
  }
}
