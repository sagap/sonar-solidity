package org.sonarsource.solidity.externalreport;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SoliumKeyUtilsTest {

  @Test
  public void test() {
    assertThat(SoliumKeyUtils.keyExists(("imports-on-top"))).isTrue();
    assertThat(SoliumKeyUtils.keyExists("security/no-unreachable-code")).isTrue();
    assertThat(SoliumKeyUtils.keyExists("no_rule")).isFalse();
  }
}
