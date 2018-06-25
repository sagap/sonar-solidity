package org.sonarsource.solidity;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.SolidityParser.BreakStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContinueStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.DoWhileStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ForStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.WhileStatementContext;

public class CognitiveComplexityVisitor extends SolidityBaseVisitor<Token> {
  private int complexity;
  private int nestingLevel;

  public CognitiveComplexityVisitor(SourceUnitContext sourceUnitCtx) {
    sourceUnitCtx.accept(this);
  }

  @Override
  public Token visitFunctionDefinition(FunctionDefinitionContext ctx) {
    complexity = 0;
    nestingLevel = 0;
    super.visitFunctionDefinition(ctx);
    return null;
  }

  @Override
  public Token visitForStatement(ForStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    // System.out.println("For: " + complexity + " nestingLevel: " + nestingLevel);
    super.visitForStatement(ctx);
    nestingLevel--;
    // System.out.println("Leaving For Loop " + nestingLevel);
    return null;
  }

  @Override
  public Token visitWhileStatement(WhileStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    // System.out.println("While: " + complexity + " nestingLevel: " + nestingLevel);
    // checkExpressionIncrementsComplexity(ctx.expression());
    // System.out.println("While: " + complexity + " nestingLevel: " + nestingLevel);
    super.visitWhileStatement(ctx);
    nestingLevel--;
    // System.out.println("Leaving While Loop " + nestingLevel);
    return null;
  }

  @Override
  public Token visitDoWhileStatement(DoWhileStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    // System.out.println("DoWhile: " + complexity + " nestinglEvel: " + nestingLevel);
    // checkExpressionIncrementsComplexity(ctx.expression());
    super.visitDoWhileStatement(ctx);
    nestingLevel--;
    // System.out.println("Leaving DoWhile Loop " + nestingLevel);
    return null;
  }

  @Override
  public Token visitIfStatement(IfStatementContext ctx) {
    // System.out.println(ctx.If() + " ::: " + ctx.If().getText() + " --- " + ctx.Else());
    if (ctx.Else() != null) {
      // System.out.println(ctx.Else().getSymbol());
    }
    if (UtilsSensor.isElseIfStatement(ctx)) {
      complexity++;
      // System.out.println("ELSE If: " + complexity);
      // checkExpressionIncrementsComplexity(ctx.expression());
      UtilsSensor.checkForElseStatement(ctx).ifPresent(elseStmt -> {
        complexity++;
      });
      return super.visitIfStatement(ctx);
    } else {
      nestingLevel++;
      complexity += nestingLevel;
      // System.out.println("If: " + complexity);
      // checkExpressionIncrementsComplexity(ctx.expression());
      UtilsSensor.checkForElseStatement(ctx).ifPresent(elseStmt -> {
        complexity++;
        // System.out.println("ELSE: " + complexity);
      });
      super.visitIfStatement(ctx);
      nestingLevel--;
      // System.out.println("Leaving IF: " + nestingLevel);
      return null;
    }
  }

  @Override
  public Token visitBreakStatement(BreakStatementContext ctx) {
    complexity += 1;
    return super.visitBreakStatement(ctx);
  }

  @Override
  public Token visitContinueStatement(ContinueStatementContext ctx) {
    complexity += 1;
    return super.visitContinueStatement(ctx);
  }

  private boolean checkExpressionIncrementsComplexity(ExpressionContext exprList) {
    for (ParseTree tree : exprList.children) {
      if (TerminalNode.class.isInstance(tree) && isAndOrOperator(tree)) {
        complexity++;
      }
    }
    return false;
  }

  private static int countConditionalOperators(ExpressionContext expr) {
    int count = 1;
    for (ParseTree tree : expr.children) {
      if (TerminalNode.class.isInstance(tree) && isAndOrOperator(tree)) {
        count++;
      }
    }
    return count;
  }

  @Override
  public Token visitTerminal(TerminalNode node) {
    if (isAndOrOperator(node)) {
      complexity++;
    }
    return super.visitTerminal(node);

  }

  @Override
  public Token visitStatement(StatementContext ctx) {
    if (UtilsSensor.isTernaryExpression(ctx)) {
      // ExpressionContext expression = Utils.countTernaryExpressionOperators(ctx);
      // complexity += countConditionalOperators(expression);
      complexity++;
    }
    return super.visitStatement(ctx);
  }

  private static boolean isAndOrOperator(ParseTree tree) {
    int type = ((CommonToken) tree.getPayload()).getType();
    return SolidityParser.CONDITIONAL_AND == type || SolidityParser.CONDITIONAL_OR == type;
  }

  public int getCognitiveComplexity() {
    return complexity;
  }
}
