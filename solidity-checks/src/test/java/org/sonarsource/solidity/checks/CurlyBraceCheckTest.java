package org.sonarsource.solidity.checks;

import org.junit.Test;

public class CurlyBraceCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new CurlyBraceCheck(), "src/test/resources/CurlyBraceCheck/test.sol");
  }
}
