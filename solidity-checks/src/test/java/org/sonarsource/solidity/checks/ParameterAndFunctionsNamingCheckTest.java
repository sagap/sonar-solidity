package org.sonarsource.solidity.checks;

import org.junit.Test;

public class ParameterAndFunctionsNamingCheckTest {

  @Test
  public void test() {
    new CheckVerifier(new ParameterAndFunctionsNamingCheck(), "src/test/resources/ParameterAndFunctionsNamingCheck/test.sol");
  }
}
