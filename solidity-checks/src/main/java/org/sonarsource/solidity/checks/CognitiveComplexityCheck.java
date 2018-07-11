package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;

@Rule(key = CognitiveComplexityCheck.RULE_KEY)
public class CognitiveComplexityCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule9";

  private static final int MAX = 10;

  @RuleProperty(key = "Threshold",
    description = "Cognitive Complexity Threshold",
    defaultValue = "" + MAX)
  private CognitiveComplexityVisitor cognitiveComplexityVisitor = new CognitiveComplexityVisitor();

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    if (ctx.block() != null) {
      ctx.accept(cognitiveComplexityVisitor);
      int total = cognitiveComplexityVisitor.getCognitiveComplexityOfFunction(ctx);
      if (total > MAX) {
        ruleContext().addIssue(ctx.getStart(), ctx.block().getStart(),
          "Refactor this method to reduce its Cognitive Complexity from " + total + " to the " + MAX + " allowed.",
          RULE_KEY);
      }
    }
    return super.visitFunctionDefinition(ctx);
  }
}
