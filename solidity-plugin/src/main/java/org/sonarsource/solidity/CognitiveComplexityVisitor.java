package org.sonarsource.solidity;

import java.util.List;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;
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

  public CognitiveComplexityVisitor(SourceUnitContext sourceUnitCtx) {
    sourceUnitCtx.accept(this);
  }

  @Override
  public Token visitFunctionDefinition(FunctionDefinitionContext ctx) {
    complexity = 0;
    System.out.println("Starts counting..." + ctx.getText() + " --- " + complexity);
    super.visitFunctionDefinition(ctx);
    System.out.println("Ends counting... " + complexity);
    return null;
  }

  @Override
  public Token visitForStatement(ForStatementContext ctx) {
    complexity += (complexity + 1);
    System.out.println("For: " + complexity);
    // checkExpressionIncrementsComplexity(ctx.expression());
    return super.visitForStatement(ctx);
  }

  @Override
  public Token visitWhileStatement(WhileStatementContext ctx) {
    complexity += (complexity + 1);
    System.out.println(ctx.expression());
    System.out.println("while: " + complexity);
    checkExpressionIncrementsComplexity(ctx.expression());
    return super.visitWhileStatement(ctx);
  }

  @Override
  public Token visitDoWhileStatement(DoWhileStatementContext ctx) {
    complexity += (complexity + 1);
    System.out.println("DoWhile: " + complexity);
    checkExpressionIncrementsComplexity(ctx.expression());
    return super.visitDoWhileStatement(ctx);
  }

  @Override
  public Token visitIfStatement(IfStatementContext ctx) {
    complexity += (complexity + 1);
    System.out.println("If: " + complexity);
    checkExpressionIncrementsComplexity(ctx.expression());
    return super.visitIfStatement(ctx);
  }

  @Override
  public Token visitBreakStatement(BreakStatementContext ctx) {
    complexity += (complexity + 1);
    System.out.println("Break: " + complexity);
    return super.visitBreakStatement(ctx);
  }

  @Override
  public Token visitContinueStatement(ContinueStatementContext ctx) {
    complexity += (complexity + 1);
    System.out.println("Continue: " + complexity);
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

  private Token visitNested(List<StatementContext> statements) {
    for (StatementContext stmt : statements) {
      super.visitStatement(stmt);
    }
    return null;
  }

  private static boolean isAndOrOperator(ParseTree tree) {
    int type = ((CommonToken) tree.getPayload()).getType();
    return 69 == type || 70 == type;
  }

}
