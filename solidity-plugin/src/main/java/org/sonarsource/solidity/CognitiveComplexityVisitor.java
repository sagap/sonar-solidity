package org.sonarsource.solidity;

import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.SolidityParser.BreakStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContinueStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.DoWhileStatementContext;
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
  protected Map<String, Integer> functionsComplexity;

  public CognitiveComplexityVisitor(SourceUnitContext sourceUnitCtx) {
    functionsComplexity = new HashMap<>();
    sourceUnitCtx.accept(this);
  }

  @Override
  public Token visitFunctionDefinition(FunctionDefinitionContext ctx) {
    complexity = 0;
    nestingLevel = 0;
    super.visitFunctionDefinition(ctx);
    functionsComplexity.put(Utils.returnFunctionSignature(ctx), complexity);
    return null;
  }

  @Override
  public Token visitForStatement(ForStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    super.visitForStatement(ctx);
    nestingLevel--;
    return null;
  }

  @Override
  public Token visitWhileStatement(WhileStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    super.visitWhileStatement(ctx);
    nestingLevel--;
    return null;
  }

  @Override
  public Token visitDoWhileStatement(DoWhileStatementContext ctx) {
    nestingLevel++;
    complexity += nestingLevel;
    super.visitDoWhileStatement(ctx);
    nestingLevel--;
    return null;
  }

  @Override
  public Token visitIfStatement(IfStatementContext ctx) {
    if (UtilsSensor.isElseIfStatement(ctx)) {
      complexity++;
      UtilsSensor.checkForElseStatement(ctx).ifPresent(elseStmt -> {
        complexity++;
      });
      return super.visitIfStatement(ctx);
    } else {
      nestingLevel++;
      complexity += nestingLevel;
      UtilsSensor.checkForElseStatement(ctx).ifPresent(elseStmt -> {
        complexity++;
      });
      super.visitIfStatement(ctx);
      nestingLevel--;
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
