package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;

@Rule(key = TransferEtherSecurelyCheck.RULE_KEY)
public class TransferEtherSecurelyCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule20";

  @Override
  public ParseTree visitFunctionCall(FunctionCallContext ctx) {
    SendIdentifierVisitor identifierVisitor = new SendIdentifierVisitor();
    ctx.accept(identifierVisitor);
    if (identifierVisitor.shouldReport) {
      ruleContext().addIssue(ctx, "You should use \"transfer()\", instead of \"send()\"", RULE_KEY);
    }
    return super.visitFunctionCall(ctx);
  }

  private static class SendIdentifierVisitor extends IssuableVisitor {

    protected boolean shouldReport = false;

    @Override
    public ParseTree visitIdentifier(IdentifierContext ctx) {
      if ("send".equals(ctx.getText())) {
        shouldReport = true;
      }
      return super.visitIdentifier(ctx);
    }
  }
}
