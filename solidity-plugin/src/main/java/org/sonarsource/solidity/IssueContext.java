package org.sonarsource.solidity;

public interface IssueContext {
  /*
   * public static void reportIssue(SolidityIssue issue, InputFile file, SensorContext context) {
   * Token start = issue.start;
   * Token stop = issue.stop;
   * String reportMessage = issue.message;
   * RuleKey ruleKey = issue.ruleKey;
   * NewIssue newIssue = context.newIssue().forRule(ruleKey).gap(Double.valueOf(1));
   * NewIssueLocation location = newIssue.newLocation()
   * .on(file).message(reportMessage);
   * DefaultTextPointer df1 = new DefaultTextPointer(start.getLine(), start.getCharPositionInLine());
   * DefaultTextPointer df2 = new DefaultTextPointer(stop.getLine(), stop.getCharPositionInLine());
   * DefaultTextRange range = new DefaultTextRange(df1, df2);
   * location.at(range);
   * newIssue.at(location);
   * newIssue.save();
   * }
   */
}
