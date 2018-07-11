package org.sonarsource.solidity.checks;

import org.junit.Test;

public class CognitiveComplexityCheckTest {

  @Test
  public void test2() {
    new CheckVerifier(new CognitiveComplexityCheck(), "src/test/resources/CognitiveComplexityCheck/test.sol");
  }
}
