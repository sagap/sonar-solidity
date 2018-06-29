package org.sonarsource.solidity.checks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextRange;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.solidity.frontend.SolidityBaseVisitor;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;

public class IssuableVisitor extends SolidityBaseVisitor<ParseTree> {

  // List of NewIssues
  public static List<SolidityIssue> issueList = new ArrayList<>();
  private SensorContextTester context = SensorContextTester.create(new File("src/resources").getAbsoluteFile());

  /*
   * Reporting for Sonarqube
   */

  public void reportIssue(Token start, Token stop, String reportMessage, String externalRuleKey, InputFile file) {
    SolidityIssue issue = new SolidityIssue(start, stop, reportMessage, externalRuleKey);
    NewIssue newIssue = context.newIssue().forRule(issue.ruleKey);
    NewIssueLocation location = newIssue.newLocation()
      .on(file).message(reportMessage);
    DefaultTextPointer df1 = new DefaultTextPointer(start.getLine(), start.getCharPositionInLine());
    DefaultTextPointer df2 = new DefaultTextPointer(stop.getLine(), stop.getCharPositionInLine());
    DefaultTextRange range = new DefaultTextRange(df1, df2);
    location.at(range);
    newIssue.at(location);
    newIssue.save();

    issueList.add(issue);
  }

  /**
   * Reporting for Testing
   * */
  public void reportIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
    SolidityIssue issue = new SolidityIssue(start, stop, reportMessage, externalRuleKey);
    issueList.add(issue);
  }

  public static void reportTest(InputFile file, SensorContext context, SourceUnitContext suc) {
    System.out.println("AAAAAAAAAAEEEEEEEEEEEE");
    // Add Issues to List
    // Then return list to sensor to report
    if (suc.pragmaDirective().isEmpty()) {
      System.out.println("AAA RRReport!");
      RuleKey ruleKey = RuleKey.of("solidity-solidity", "ExampleRule1");
      NewIssue newIssue = context.newIssue().forRule(ruleKey).gap(Double.valueOf(1));
      NewIssueLocation location = newIssue.newLocation()
        .on(file).message("AAA message");
      location.at(file.selectLine(1));
      newIssue.at(location);
      newIssue.save();
      System.out.println(newIssue);
    }
  }
}
