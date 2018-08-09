package org.sonarsource.solidity;

import java.util.Set;
import java.util.stream.IntStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.SolidityParser.BreakStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContinueStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.DoWhileStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.EmitStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ForStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ReturnStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.frontend.SolidityParser.ThrowStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.WhileStatementContext;
import org.sonarsource.solidity.frontend.SolidityParsingPhase;
import org.sonarsource.solidity.frontend.Utils;

public class MetricsVisitor extends SolidityBaseVisitor<Token> {
  protected FileMeasures fileMeasures;
  int functionCounter = 0;
  int statements = 0;
  int contractCounter = 0;

  public MetricsVisitor(SolidityParsingPhase parser, SourceUnitContext suc) {
    this.fileMeasures = new FileMeasures();
    suc.accept(this);
    this.fileMeasures.setStatementNumber(statements);
    this.fileMeasures.setFunctionNumber(functionCounter);
    this.fileMeasures.setContractNumber(contractCounter);
    this.fileMeasures.setCommentLinesNumber(parser.getLinesOfComments());
    TerminalNode node = suc.EOF();
    this.fileMeasures.setLinesOfCodeNumber(node.getSymbol().getLine() - computeLinesOfComments(parser.comments) - parser.getEmptyLines());
    // TODO fix complexity
    this.fileMeasures.setContractComplexity(0);
    this.fileMeasures.setFunctionComplexity(0);
    this.fileMeasures.setFileComplexity(0);
    this.fileMeasures.setFileCognitiveComplexity(0);
  }

  private int computeLinesOfComments(Set<Token> tokens) {
    int comments = 0;
    for (Token token : tokens) {
      if (token.getType() == SolidityParser.COMMENT) {
        if (Utils.isCommentSignificant(token)) {
          comments++;
        }
      } else {
        String[] comment = token.getText().split("\r\n|\r|\n");
        comments += IntStream.range(0, comment.length)
          .filter(x -> Utils.isCommentSignificant(comment[x]))
          .count();
      }
    }
    return comments;
  }

  @Override
  public Token visitContractDefinition(ContractDefinitionContext ctx) {
    contractCounter++;
    return super.visitContractDefinition(ctx);
  }

  @Override
  public Token visitFunctionDefinition(FunctionDefinitionContext ctx) {
    functionCounter++;
    return super.visitFunctionDefinition(ctx);
  }

  @Override
  public Token visitBreakStatement(BreakStatementContext ctx) {
    statements++;
    return super.visitBreakStatement(ctx);
  }

  @Override
  public Token visitContinueStatement(ContinueStatementContext ctx) {
    statements++;
    return super.visitContinueStatement(ctx);
  }

  @Override
  public Token visitDoWhileStatement(DoWhileStatementContext ctx) {
    statements++;
    return super.visitDoWhileStatement(ctx);
  }

  @Override
  public Token visitEmitStatement(EmitStatementContext ctx) {
    statements++;
    return super.visitEmitStatement(ctx);
  }

  @Override
  public Token visitReturnStatement(ReturnStatementContext ctx) {
    statements++;
    return super.visitReturnStatement(ctx);
  }

  @Override
  public Token visitExpressionStatement(ExpressionStatementContext ctx) {
    statements++;
    return super.visitExpressionStatement(ctx);
  }

  @Override
  public Token visitForStatement(ForStatementContext ctx) {
    statements++;
    return super.visitForStatement(ctx);
  }

  @Override
  public Token visitIfStatement(IfStatementContext ctx) {
    statements++;
    return super.visitIfStatement(ctx);
  }

  @Override
  public Token visitThrowStatement(ThrowStatementContext ctx) {
    statements++;
    return super.visitThrowStatement(ctx);
  }

  @Override
  public Token visitVariableDeclarationStatement(VariableDeclarationStatementContext ctx) {
    statements++;
    return super.visitVariableDeclarationStatement(ctx);
  }

  @Override
  public Token visitWhileStatement(WhileStatementContext ctx) {
    statements++;
    return super.visitWhileStatement(ctx);
  }
}
