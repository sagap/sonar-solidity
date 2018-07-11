package org.sonarsource.solidity.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;

@Rule(key = CognitiveComplexityCheck.RULE_KEY)
public class CognitiveComplexityCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule9";

  private static final int DEFAULT_MAX = 10;

  @RuleProperty(key = "Threshold",
    description = "Cognitive Complexity Threshold",
    defaultValue = "" + DEFAULT_MAX)

  private int max = DEFAULT_MAX;

  @Override
  public ParseTree visitFunctionDefinition(FunctionDefinitionContext ctx) {
    if (ctx.block() != null) {
      CognitiveComplexityVisitor cognitiveComplexityVisitor = new CognitiveComplexityVisitor();
      ctx.accept(cognitiveComplexityVisitor);
      int total = cognitiveComplexityVisitor.getCognitiveComplexityOfFunction(ctx);
      if (total > max) {
        ruleContext().addIssue(ctx.getStart(), ctx.block().getStart(),
          "Refactor this method to reduce its Cognitive Complexity from " + total + " to the " + max + " allowed.",
          RULE_KEY);
      }
    }
    return super.visitFunctionDefinition(ctx);
  }
}
