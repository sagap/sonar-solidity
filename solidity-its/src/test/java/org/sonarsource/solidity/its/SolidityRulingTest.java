package org.sonarsource.solidity.its;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityRulingTest {

  @Test
  public void test() {
    assertThat(SolidityRuling.getProjects()).hasSize(2);
    SolidityRuling.deletePreviouslyAnalyzedFiles();
    SolidityRuling.collectSolidityFiles();
  }
}
