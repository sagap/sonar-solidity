package org.sonarsource.solidity.checks;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;

public class IssuableVisitor extends SolidityBaseVisitor<ParseTree> {

  // List of NewIssues
  public List<SolidityIssue> issueList = new ArrayList<>();
  public RuleContext ruleContext;

  /**
   * Reporting for Testing
   * */
  public void reportIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
    SolidityIssue issue = new SolidityIssue(start, stop, reportMessage, externalRuleKey);
    issueList.add(issue);
  }

  public void setRuleContext(RuleContext ruleContext) {
    this.ruleContext = ruleContext;
  }

  public final RuleContext ruleContext() {
    return ruleContext;
  }
}
