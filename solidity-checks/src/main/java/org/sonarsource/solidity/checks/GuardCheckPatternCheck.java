package org.sonarsource.solidity.checks;

import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallArgumentsContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierInvocationContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierListContext;
import org.sonarsource.solidity.frontend.SolidityParser.ParameterContext;
import org.sonarsource.solidity.frontend.SolidityParser.SimpleStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;

@Rule(key = GuardCheckPatternCheck.RULE_KEY)
public class GuardCheckPatternCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule17";

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    ModifierListContext modifiersList = ctx.modifierList();
    if (CheckUtils.isPublicOrExternalFunction(modifiersList)
      && !CheckUtils.isViewOrPureFunction(modifiersList.stateMutability()) && ctx.block() != null) {
      List<ParameterContext> parameters = ctx.parameterList().parameter();
      for (ParameterContext parameter : parameters) {
        if (parameterIsNotGuardChecked(parameter.identifier(), modifiersList.modifierInvocation(), ctx.block().statement())) {
          ruleContext().addIssue(ctx.identifier(), "You should check with require the validity of all the parameters.", RULE_KEY);
          break;
        }
      }
    }
    return super.visitFunctionDefinition(ctx);
  }

  private static boolean parameterIsNotGuardChecked(IdentifierContext parameter,
    List<ModifierInvocationContext> modifierInvocationList, List<StatementContext> statementList) {
    if (parameter != null) {
      if (argumentCheckedInModifier(modifierInvocationList, parameter.getText())) {
        return false;
      }
      return !argumentCheckedInFunction(statementList, parameter.getText());
    }
    return false;
  }

  private static boolean argumentCheckedInFunction(List<StatementContext> statementList, String parameter) {
    for (StatementContext statement : statementList) {
      Optional<FunctionCallArgumentsContext> arguments = statementIsRequireFunctionCall(statement);
      if (arguments.isPresent()) {
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(parameter);
        arguments.get().accept(expressionVisitor);
        if (!expressionVisitor.shouldReport) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean argumentCheckedInModifier(List<ModifierInvocationContext> modifierInvocationList, String parameter) {
    for (ModifierInvocationContext modifierInvocation : modifierInvocationList) {
      if (modifierInvocation.expressionList() != null) {
        for (ExpressionContext expression : modifierInvocation.expressionList().expression()) {
          if (parameter.equals(expression.getText())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private static Optional<FunctionCallArgumentsContext> statementIsRequireFunctionCall(StatementContext stmt) {
    FunctionCallArgumentsContext args = null;
    SimpleStatementContext simpleStmt = stmt.simpleStatement();
    if (simpleStmt != null && simpleStmt.expressionStatement() != null) {
      FunctionCallContext functionCall = simpleStmt.expressionStatement().expression().functionCall();
      if (functionCall != null
        && "require".equals(functionCall.identifier(0).getText())) {
        args = functionCall.functionCallArguments();
      }
    }
    return Optional.ofNullable(args);
  }

  private static class ExpressionVisitor extends IssuableVisitor {

    private final String parameter;
    protected boolean shouldReport;

    public ExpressionVisitor(String parameter) {
      this.parameter = parameter;
      this.shouldReport = true;
    }

    @Override
    public ParseTree visitExpression(ExpressionContext ctx) {
      if (parameter.equals(ctx.getText())) {
        this.shouldReport = false;
      }
      return super.visitExpression(ctx);
    }
  }
}
