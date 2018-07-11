package org.sonarsource.solidity;

import org.antlr.v4.runtime.Token;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextRange;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.solidity.checks.RuleContext;

/* 
 * 
 * class used to report on SonarQube 
 * 
 * */
public class SolidityRuleContext implements RuleContext {

  public static final String REPO_KEY = "solidity-solidity";

  private InputFile file;
  private SensorContext context;

  public SolidityRuleContext(InputFile file, SensorContext context) {
    this.file = file;
    this.context = context;
  }

  @Override
  public void addIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
    RuleKey ruleKey = RuleKey.of(REPO_KEY, externalRuleKey);
    NewIssue newIssue = context.newIssue().forRule(ruleKey).gap(Double.valueOf(1));
    NewIssueLocation location = newIssue.newLocation()
      .on(file).message(reportMessage);
    DefaultTextPointer df1 = new DefaultTextPointer(start.getLine(), start.getCharPositionInLine());
    DefaultTextPointer df2 = new DefaultTextPointer(stop.getLine(), stop.getCharPositionInLine());
    DefaultTextRange range = new DefaultTextRange(df1, df2);
    location.at(range);
    newIssue.at(location);
    newIssue.save();
  }

  @Override
  public void addIssueOnFile(String reportMessage, String externalRuleKey) {
    RuleKey ruleKey = RuleKey.of(REPO_KEY, externalRuleKey);
    NewIssue newIssue = context.newIssue().forRule(ruleKey).gap(Double.valueOf(1));
    NewIssueLocation location = newIssue.newLocation()
      .on(file).message(reportMessage);
    newIssue.at(location);
    newIssue.save();
  }

  @Override
  public void addIssue(Token start, Token stop, int offset, String reportMessage, String externalRuleKey) {
    RuleKey ruleKey = RuleKey.of(REPO_KEY, externalRuleKey);
    NewIssue newIssue = context.newIssue().forRule(ruleKey).gap(Double.valueOf(1));
    NewIssueLocation location = newIssue.newLocation()
      .on(file).message(reportMessage);
    DefaultTextPointer df1 = new DefaultTextPointer(start.getLine(), start.getCharPositionInLine());
    DefaultTextPointer df2 = new DefaultTextPointer(start.getLine(), stop.getCharPositionInLine() + offset);
    DefaultTextRange range = new DefaultTextRange(df1, df2);

    location.at(range);
    newIssue.at(location);
    newIssue.save();
  }
}
