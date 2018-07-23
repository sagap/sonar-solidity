package org.sonarsource.solidity.checks;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.BlockContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractPartContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionCallContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.InheritanceSpecifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.SimpleStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;

@Rule(key = OraclePatternCheck.RULE_KEY)
public class OraclePatternCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule19";
  private static final String CALLBACK_FUNCTION = "__callback";

  @Override
  public ParseTree visitContractDefinition(ContractDefinitionContext ctx) {

    List<ContractPartContext> contractPart = ctx.contractPart();
    if (inheritOraclizeOracle(ctx.inheritanceSpecifier()) && contractPart != null) {
      List<FunctionDefinitionContext> callbackFunctions = contractPart.stream()
        .map(ContractPartContext::functionDefinition)
        .filter(Objects::nonNull)
        .filter(function -> function.identifier() != null)
        .filter(this::isCallbackFunction)
        .collect(Collectors.toList());

      for (FunctionDefinitionContext function : callbackFunctions) {
        BlockContext functionBlock = function.block();
        if (functionBlock != null &&
          functionBlock.statement().stream()
            .filter(this::statementIsRequireFunctionCall)
            .count() == 0) {
          ruleContext().addIssue(function.identifier(), "Callback function should use require to check "
            + "if the results from the external call are valid.", RULE_KEY);
        }
      }
    }
    return super.visitContractDefinition(ctx);

  }

  private boolean statementIsRequireFunctionCall(StatementContext stmt) {
    SimpleStatementContext simpleStmt = stmt.simpleStatement();
    if (simpleStmt != null && simpleStmt.expressionStatement() != null) {
      FunctionCallContext functionCall = simpleStmt.expressionStatement().expression().functionCall();
      if (functionCall != null) {
        String functionCallIdentifier = functionCall.identifier(0).getText();
        if (CALLBACK_FUNCTION.equals(functionCallIdentifier)
          || "require".equals(functionCallIdentifier)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isCallbackFunction(FunctionDefinitionContext function) {
    return CALLBACK_FUNCTION.equals(function.identifier().getText());
  }

  private static boolean inheritOraclizeOracle(List<InheritanceSpecifierContext> inheritance) {
    return inheritance.stream()
      .filter(identifier -> "usingOraclize".equals(identifier.getText()))
      .count() == 1;
  }
}
