package org.sonarsource.solidity.checks;

import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;

@Rule(key=StringEqualityComparisonCheck.RULE_KEY)
public class StringEqualityComparisonCheck extends IssuableVisitor{

	public static final String RULE_KEY = "ExternalRule18";

	@Override
	public ParseTree visitExpression(ExpressionContext ctx) {
		if(ctx.functionCall()!= null && isStringUtilsLibrary(ctx.functionCall())) {
				ruleContext().addIssue(ctx.functionCall(), "You should use the hash function \"keccak256()\" for string equality.", RULE_KEY);
		}
		return super.visitExpression(ctx);
	}

	private static boolean isStringUtilsLibrary(FunctionCallContext functionCallCtx) {
		List<IdentifierContext> identifiers = functionCallCtx.identifier();
		return !identifiers.isEmpty() && "StringUtils".equals(identifiers.get(0).getText()) && "equal".equals(identifiers.get(1).getText());
	}
}
