package org.sonarsource.solidity.checks;

import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.BlockContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.DoWhileStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.EnumDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ForStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StructDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.WhileStatementContext;

@Rule(key = CurlyBraceCheck.RULE_KEY)
public class CurlyBraceCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule6";

  @Override
  public ParseTree visitContractDefinition(ContractDefinitionContext ctx) {
    Token curlyBraceToken = CheckUtils.getOpenCurlyBrace(ctx).getSymbol();
    if (ctx.getStart().getLine() != curlyBraceToken.getLine()) {
      report(curlyBraceToken);
    }
    return super.visitContractDefinition(ctx);
  }

  @Override
  public ParseTree visitIfStatement(IfStatementContext ctx) {
    BlockContext block = ctx.statement(0).block();
    if (block != null) {
      int type = CheckUtils.returnTtypeFromLiteralName("'{'");
      Token curlyBraceToken = block.getToken(type, 0).getSymbol();
      if (curlyBraceToken.getLine() != ctx.expression().getStop().getLine()) {
        report(curlyBraceToken);
      }
    }
    CheckUtils.checkForElseStatement(ctx).ifPresent(stmtElse -> {
      StatementContext stmt = (StatementContext) stmtElse;
      if (stmt.block() != null) {
        int type = CheckUtils.returnTtypeFromLiteralName("'{'");
        Token curlyBraceToken = stmt.block().getToken(type, 0).getSymbol();
        TerminalNode elseNode = CheckUtils.findTerminalNode(ctx, "'else'");
        if (curlyBraceToken.getLine() != elseNode.getSymbol().getLine()) {
          report(curlyBraceToken);
        }
      }
    });
    return super.visitIfStatement(ctx);
  }

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    BlockContext functionBlock = ctx.block();
    int functionLastLine = ctx.modifierList().getStop().getLine();
    if (ctx.returnParameters() != null) {
      functionLastLine = ctx.returnParameters().getStop().getLine();
    }
    if (functionBlock != null) {
      Token curlyBraceToken = (Token) functionBlock.getChild(0).getPayload();
      if (curlyBraceToken.getLine() != functionLastLine) {
        report(curlyBraceToken);
      }
    }
    return super.visitFunctionDefinition(ctx);
  }

  @Override
  public ParseTree visitForStatement(ForStatementContext ctx) {
    StatementContext statementList = ctx.statement();
    if (CheckUtils.treeMatches(statementList.getChild(0), BlockContext.class)) {
      List<ExpressionContext> expressionList = ctx.expression();
      Token curlyBraceToken = (Token) statementList.block().getChild(0).getPayload();
      if (curlyBraceToken.getLine() != expressionList.get(expressionList.size() - 1).getStop().getLine()) {
        report(curlyBraceToken);
      }
    }
    return super.visitForStatement(ctx);
  }

  @Override
  public ParseTree visitDoWhileStatement(DoWhileStatementContext ctx) {
    StatementContext statementList = ctx.statement();
    if (CheckUtils.treeMatches(statementList.getChild(0), BlockContext.class)) {
      TerminalNode doKeyword = CheckUtils.findTerminalNode(ctx, "'do'");
      Token curlyBraceToken = (Token) statementList.block().getChild(0).getPayload();
      if (doKeyword.getSymbol().getLine() != curlyBraceToken.getLine()) {
        report(curlyBraceToken);
      }
    }
    return super.visitDoWhileStatement(ctx);
  }

  @Override
  public ParseTree visitWhileStatement(WhileStatementContext ctx) {
    StatementContext statementList = ctx.statement();
    if (CheckUtils.treeMatches(statementList.getChild(0), BlockContext.class)) {
      ExpressionContext expression = ctx.expression();
      Token curlyBraceToken = (Token) statementList.block().getChild(0).getPayload();
      if (expression.getStop().getLine() != curlyBraceToken.getLine()) {
        report(curlyBraceToken);
      }
    }
    return super.visitWhileStatement(ctx);
  }

  @Override
  public ParseTree visitStructDefinition(StructDefinitionContext ctx) {
    Token curlyBraceToken = CheckUtils.getOpenCurlyBrace(ctx).getSymbol();
    if (ctx.identifier().getStart().getLine() != curlyBraceToken.getLine()) {
      report(curlyBraceToken);
    }
    return super.visitStructDefinition(ctx);
  }

  @Override
  public ParseTree visitModifierDefinition(ModifierDefinitionContext ctx) {
    Token curlyBraceToken = (Token) ctx.block().getChild(0).getPayload();
    int lastLine = ctx.identifier().Identifier().getSymbol().getLine();
    if (ctx.parameterList() != null) {
      lastLine = ctx.parameterList().getStop().getLine();
    }
    if (lastLine != curlyBraceToken.getLine()) {
      report(curlyBraceToken);
    }
    return super.visitModifierDefinition(ctx);
  }

  @Override
  public ParseTree visitEnumDefinition(EnumDefinitionContext ctx) {
    Token curlyBraceToken = CheckUtils.getOpenCurlyBrace(ctx).getSymbol();
    if (ctx.identifier().getStop().getLine() != curlyBraceToken.getLine()) {
      report(curlyBraceToken);
    }
    return super.visitEnumDefinition(ctx);
  }

  private void report(Token curlyBraceToken) {
    ruleContext().addIssue(curlyBraceToken, curlyBraceToken, 1, "Move this open curly brace to the end of the previous line.", RULE_KEY);
  }
}
