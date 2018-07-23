package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.TypeNameContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationStatementContext;

@Rule(key = RandomnessCheck.RULE_KEY)
public class RandomnessCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule21";

  @Override
  public ParseTree visitVariableDeclarationStatement(VariableDeclarationStatementContext ctx) {
    if (ctx.variableDeclaration() != null && lHSIsUint(ctx.variableDeclaration())) {
      ExpressionVisitor visitor = new ExpressionVisitor();
      if (ctx.expression() != null) {
        ctx.expression().accept(visitor);
        if (visitor.expr != null) {
          ruleContext().addIssue(visitor.expr, "You should not use block to create a random number, "
            + "you should use \"keccak256()\".", RULE_KEY);
        }
      }
    }
    return super.visitVariableDeclarationStatement(ctx);
  }

  private static boolean lHSIsUint(VariableDeclarationContext varDeclaration) {
    TypeNameContext varType = varDeclaration.typeName();
    return varType.getText().startsWith("uint");
  }

  private static class ExpressionVisitor extends IssuableVisitor {

    private ParserRuleContext expr = null;
    private boolean keccak256IsUsed = false;

    @Override
    public ParseTree visitExpression(ExpressionContext ctx) {
      if (!keccak256IsUsed && "block.number".equals(ctx.getText())) {
        expr = ctx;
      }
      return super.visitExpression(ctx);
    }

    @Override
    public ParseTree visitFunctionCall(FunctionCallContext ctx) {
      if (ctx.identifier(0) != null) {
        if ("keccak256".equals(ctx.identifier(0).getText())) {
          keccak256IsUsed = true;
        } else {
          if (!keccak256IsUsed
            && ("block".equals(ctx.identifier(0).getText())
              && "blockhash".equals(ctx.identifier(1).getText()))
            || "blockhash".equals(ctx.identifier(0).getText())) {
            expr = ctx;
          }
        }
      }
      return super.visitFunctionCall(ctx);
    }
  }
}
