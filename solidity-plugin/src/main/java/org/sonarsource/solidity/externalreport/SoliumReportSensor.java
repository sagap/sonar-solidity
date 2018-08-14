package org.sonarsource.solidity.externalreport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.sonar.api.rules.RuleType;

public class SoliumReportSensor extends AbstractExternalReportSensor {

  public static final String PROPERTY_KEY = "sonar.solidity.solium.reportPaths";

  private static final Pattern SOLIUM_FILE_REGEX = Pattern.compile("(?<file>[^.]+.sol)");
  private static final Pattern SOLIUM_LINE_REGEX = Pattern.compile("\\s*((?<line>\\d+):\\d+)\\s*(?<type>\\w*)\\s*(?<message>.*[?=\\s{2}])\\s*(?<key>.*)");
  public static final String LINTER_NAME = "Solium";
  public static final String LINTER_ID = "solium";
  // Pattern used to remove special characters created by Solium linter
  private static final Pattern SOLIUM_SPECIAL_CHARACTERS = Pattern.compile(".{2,3}[\\d*]m");

  @Override
  String linterName() {
    return LINTER_NAME;
  }

  @Override
  String reportsPropertyName() {
    return PROPERTY_KEY;
  }

  @Nullable
  @Override
  ExternalIssue parse(String line) {
    Matcher m = SOLIUM_SPECIAL_CHARACTERS.matcher(line);
    String cleanLine = m.replaceAll("");
    Matcher fileMatcher = SOLIUM_FILE_REGEX.matcher(cleanLine);
    Matcher lineMatcher = SOLIUM_LINE_REGEX.matcher(cleanLine);
    if (fileMatcher.matches()) {
      String filename = fileMatcher.group("file");
      return new ExternalIssue(LINTER_ID, null, null, filename, 0, "onlyfilename");
    } else if (lineMatcher.matches()) {
      int lineNumber = Integer.parseInt(lineMatcher.group("line"));
      String issueType = lineMatcher.group("type").trim();
      String message = lineMatcher.group("message").trim();
      String issueKey = lineMatcher.group("key").trim();
      if (SoliumKeyUtils.keyExists(issueKey)) {
        return new ExternalIssue(LINTER_ID, matchIssueToRuleType(issueType, issueKey), issueKey, null, lineNumber, message);
      } else {
        return null;
      }
    } else {
      LOG.debug(logPrefix() + "Unexpected line: " + line);
    }
    return null;
  }

  private static RuleType matchIssueToRuleType(String issueType, String issueKey) {
    if ("error".equals(issueType) && issueKey.startsWith("security")) {
      return RuleType.VULNERABILITY;
    } else if ("warning".equals(issueType)) {
      return RuleType.CODE_SMELL;
    } else {
      return RuleType.BUG;
    }
  }
}
