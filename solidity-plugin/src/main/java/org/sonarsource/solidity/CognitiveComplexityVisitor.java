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
import org.sonarsource.solidity.frontend.Utils;

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
    System.out.println("Starts counting..." + ctx.getText() + " --- complexity: " + complexity + " --- level: " + nestingLevel);
    super.visitFunctionDefinition(ctx);
    System.out.println("Ends counting... complexity: " + complexity + " --- level: " + nestingLevel);
    return null;
  }

  @Override
  public Token visitForStatement(ForStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    System.out.println("For: " + complexity + " nestingLevel: " + nestingLevel);
    super.visitForStatement(ctx);
    nestingLevel--;
    System.out.println("Leaving For Loop " + nestingLevel);
    return null;
  }

  @Override
  public Token visitWhileStatement(WhileStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    System.out.println("While: " + complexity + " nestingLevel: " + nestingLevel);
    checkExpressionIncrementsComplexity(ctx.expression());
    super.visitWhileStatement(ctx);
    nestingLevel--;
    System.out.println("Leaving While Loop " + nestingLevel);
    return null;
  }

  @Override
  public Token visitDoWhileStatement(DoWhileStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    System.out.println("DoWhile: " + complexity + " nestinglEvel: " + nestingLevel);
    checkExpressionIncrementsComplexity(ctx.expression());
    super.visitDoWhileStatement(ctx);
    nestingLevel--;
    System.out.println("Leaving DoWhile Loop " + nestingLevel);
    return null;
  }

  @Override
  public Token visitIfStatement(IfStatementContext ctx) {
    nestingLevel++;
    if (Utils.isElseIfStatement(ctx)) {
      complexity++;
      System.out.println("ELSE If: " + complexity);
      Utils.checkForElseStatement(ctx).ifPresent(elseStmt -> {
        complexity++;
        super.visit(elseStmt);
      });
      super.visitIfStatement(ctx);
      nestingLevel--;
      return null;
    }
    complexity += nestingLevel;
    System.out.println("If: " + complexity);
    checkExpressionIncrementsComplexity(ctx.expression());
    super.visitIfStatement(ctx);
    Utils.checkForElseStatement(ctx).ifPresent(elseStmt -> {
      complexity++;
      System.out.println("ELSE: " + complexity);
    });
    nestingLevel--;
    System.out.println("Leaving IF: " + nestingLevel);
    return null;
  }

  @Override
  public Token visitBreakStatement(BreakStatementContext ctx) {
    complexity += 1;
    System.out.println("Break: " + complexity);
    return super.visitBreakStatement(ctx);
  }

  @Override
  public Token visitContinueStatement(ContinueStatementContext ctx) {
    complexity += 1;
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

  public int getComplexity() {
    return complexity;
  }

  public int getNestingLevel() {
    return nestingLevel;
  }
}
