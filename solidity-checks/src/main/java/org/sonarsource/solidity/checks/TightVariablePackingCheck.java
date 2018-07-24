package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.StateVariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.TypeNameContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationListContext;

@Rule(key = TightVariablePackingCheck.RULE_KEY)
public class TightVariablePackingCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule24";

  private static final ImmutableSet<String> VARIABLE_TYPES = ImmutableSet.<String>builder()
    .add("uint", "int", "bytes")
    .build();

  private static final Set<TypeNameContext> REPORTED_VARIABLE_DECLARATION = new HashSet<>();

  @Override
  public ParseTree visitVariableDeclaration(VariableDeclarationContext ctx) {
    checkToReport(ctx.typeName());
    return super.visitVariableDeclaration(ctx);
  }

  @Override
  public ParseTree visitStateVariableDeclaration(StateVariableDeclarationContext ctx) {
    checkToReport(ctx.typeName());
    return super.visitStateVariableDeclaration(ctx);
  }

  @Override
  public ParseTree visitVariableDeclarationList(VariableDeclarationListContext ctx) {
    ctx.variableDeclaration().stream()
      .forEach(variableDeclaration -> checkToReport(variableDeclaration.typeName()));
    return super.visitVariableDeclarationList(ctx);
  }

  private void checkToReport(TypeNameContext typeVariable) {
    if (variableTypeIsBytes(typeVariable)
      && !REPORTED_VARIABLE_DECLARATION.contains(typeVariable)) {
      REPORTED_VARIABLE_DECLARATION.add(typeVariable);
      ruleContext().addIssue(typeVariable, "Use a statistically sized variable, "
        + "instead of '" + typeVariable.getText() + "'", RULE_KEY);
    }
  }

  private static boolean variableTypeIsBytes(TypeNameContext varType) {
    return VARIABLE_TYPES.contains(varType.getText());
  }

}
