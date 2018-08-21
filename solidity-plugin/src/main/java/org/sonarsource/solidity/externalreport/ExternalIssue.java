package org.sonarsource.solidity.externalreport;

import org.sonar.api.rules.RuleType;

class ExternalIssue {

  final String linter;
  final RuleType type;
  final String ruleKey;
  String filename;
  final int lineNumber;
  final String message;

  ExternalIssue(String linter, RuleType type, String ruleKey, String filename, int lineNumber, String message) {
    this.linter = linter;
    this.type = type;
    this.ruleKey = ruleKey;
    this.filename = filename;
    this.lineNumber = lineNumber;
    this.message = message;
  }
}
