package org.sonarsource.solidity.checks;

import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.ImportDirectiveContext;

@Rule(key = StringEqualityComparisonCheck.RULE_KEY)
public class StringEqualityComparisonCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule18";
  private boolean stringUtilsImprorted = false;

  @Override
  public ParseTree visitImportDirective(ImportDirectiveContext ctx) {
    if (LiteralUtils.removeQuotesFromStringLiteral(ctx.StringLiteral().getText()).endsWith("stringUtils.sol")) {
      stringUtilsImprorted = true;
    }
    return super.visitImportDirective(ctx);
  }

  @Override
  public ParseTree visitExpression(ExpressionContext ctx) {
    FunctionCallContext functionCall = ctx.functionCall();
    if (stringUtilsImprorted
      && functionCall != null
      && isStringUtilsLibrary(functionCall)) {
      ruleContext().addIssue(functionCall, "You should use the hash function \"keccak256()\" for string equality.", RULE_KEY);
    }
    return super.visitExpression(ctx);
  }

  private static boolean isStringUtilsLibrary(FunctionCallContext functionCallCtx) {
    List<IdentifierContext> identifiers = functionCallCtx.identifier();
    return "StringUtils".equals(identifiers.get(0).getText())
      && "equal".equals(identifiers.get(1).getText());
  }
}
