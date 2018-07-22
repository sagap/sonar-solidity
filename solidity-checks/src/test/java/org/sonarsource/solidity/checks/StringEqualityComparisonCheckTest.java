package org.sonarsource.solidity.checks;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringEqualityComparisonCheckTest {

	@Test
	public void test() {
	    new CheckVerifier(new StringEqualityComparisonCheck(), "src/test/resources/StringEqualityComparisonCheck/test.sol");
	}
}
