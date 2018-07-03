package org.sonarsource.solidity.checks;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckUtilsTest {

  @Test
  public void test() {
    assertThat(CheckList.returnChecks()).isNotEmpty();
    assertThat(RuleKeyList.returnChecks()).isNotEmpty();
    assertThat(CheckUtils.isCommentForReporting("// ......")).isFalse();
    assertThat(CheckUtils.isCommentForReporting("// Noncompliant {{ ... }}")).isTrue();
    assertThat(CheckUtils.returnContentOfComments("// fffffff")).isNotNull();
  }
}
