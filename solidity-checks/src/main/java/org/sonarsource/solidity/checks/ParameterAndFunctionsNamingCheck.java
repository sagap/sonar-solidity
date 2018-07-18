package org.sonarsource.solidity.checks;

import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.ParameterContext;
import org.sonarsource.solidity.frontend.SolidityParser.ParameterListContext;

@Rule(key = ParameterAndFunctionsNamingCheck.RULE_KEY)
public class ParameterAndFunctionsNamingCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule11";

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    CheckUtils.extractNameFromFunction(ctx).ifPresent(functionName -> {
      ParameterListContext parameterList = ctx.parameterList();
      List<ParameterContext> parameters = parameterList.parameter();
      for (ParameterContext parameter : parameters) {
        extractNameFromParameter(parameter).ifPresent(parameterName -> {
          if (functionName.equalsIgnoreCase(parameterName)) {
            ruleContext().addIssue(parameter.getStart(), parameter.getStop(),
              "Rename the parameter " + parameterName + " so that it doesn't duplicate the method name.", RULE_KEY);
          }
        });
      }
    });
    return super.visitFunctionDefinition(ctx);
  }

  private static Optional<String> extractNameFromParameter(ParameterContext parameter) {
    IdentifierContext parameterIdentifier = parameter.identifier();
    String parameterName = null;
    if (parameterIdentifier != null) {
      parameterName = parameterIdentifier.getText();
    }
    return Optional.ofNullable(parameterName);
  }
}
