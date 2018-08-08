package org.sonarsource.solidity.checks;

import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;
import org.sonarsource.solidity.frontend.SolidityParser.BreakStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContinueStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.DoWhileStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ForStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.WhileStatementContext;

public class CognitiveComplexityVisitor extends SolidityBaseVisitor<Token> {
  private int complexity;
  private int nestingLevel;
  protected static final Map<FunctionDefinitionContext, Integer> functionsComplexity = new HashMap<>();

  public CognitiveComplexityVisitor(SourceUnitContext sourceUnitCtx) {
    sourceUnitCtx.accept(this);
  }

  public CognitiveComplexityVisitor() {
  }

  @Override
  public Token visitFunctionDefinition(FunctionDefinitionContext ctx) {
    complexity = 0;
    nestingLevel = 0;
    super.visitFunctionDefinition(ctx);
    functionsComplexity.put(ctx, complexity);
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
    if (CheckUtils.isElseIfStatement(ctx)) {
      complexity++;
      CheckUtils.checkForElseStatement(ctx).ifPresent(elseStmt -> {
        complexity++;
      });
      return super.visitIfStatement(ctx);
    } else {
      nestingLevel++;
      complexity += nestingLevel;
      CheckUtils.checkForElseStatement(ctx).ifPresent(elseStmt -> {
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
    if (CheckUtils.isTernaryExpression(ctx)) {
      complexity++;
    }
    return super.visitStatement(ctx);
  }

  private static boolean isAndOrOperator(ParseTree tree) {
    int type = ((CommonToken) tree.getPayload()).getType();
    return CheckUtils.returnTtypeFromLiteralName("'&&'") == type || CheckUtils.returnTtypeFromLiteralName("'||'") == type;
  }

  public int getCognitiveComplexity() {
    return complexity;
  }

  public int sumAllFunctionsComplexity() {
    return functionsComplexity.values().stream().mapToInt(Integer::intValue).sum();
  }

  public int getCognitiveComplexityOfFunction(FunctionDefinitionContext function) {
    return functionsComplexity.get(function);
  }
}
