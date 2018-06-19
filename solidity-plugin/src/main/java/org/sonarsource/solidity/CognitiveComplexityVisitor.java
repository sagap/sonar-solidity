package org.sonarsource.solidity;

import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
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
    System.out.println("PRIMARY: " + exprList.getChildCount());
    System.out.println(exprList.getChild(0).getText());
    System.out.println(exprList.getChild(1).getText());
    System.out.println(exprList.getChild(2).getText());
    // exprList.getRuleContexts(TerminalNode.class);
    for (ExpressionContext exp : exprList.expression()) {
      // exprList.children.forEach(x -> System.out.println(x.getText() + " ... " + x.getClass()));
      System.out.println("TEXT: " + exp.getRuleIndex());
      System.out.println(":::" + exp.getText() + " +++ " + exp.getClass().toString());
      ExpressionContext temp = (ExpressionContext) exp;
      System.out.println("TEMPPPPP: " + temp.getRuleIndex() + " --- " + temp.getText() + " " + exp.typeName());
    }
    return false;
  }

  private Token visitNested(List<StatementContext> statements) {
    for (StatementContext stmt : statements) {
      super.visitStatement(stmt);
    }
    return null;
  }

  private boolean isTerminalNode(ParseTree exp) {
    return ("class org.antlr.v4.runtime.tree.TerminalNodeImpl".equals(exp.getClass().toString()));
  }
}
