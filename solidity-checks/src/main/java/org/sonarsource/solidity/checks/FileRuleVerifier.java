package org.sonarsource.solidity.checks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.Utils;

import static org.assertj.core.api.Assertions.assertThat;

public class FileRuleVerifier {

  private FileRuleVerifier() {
    // ...
  }

  private static final Logger LOG = Loggers.get(FileRuleVerifier.class);

  private static final TestRuleContext testRuleContext = new TestRuleContext();

  private static void verifyIssueOnFile(IssuableVisitor checkVisitor, String relativePath, String reportMessage) {
    CharStream cs;
    try {
      cs = CharStreams.fromFileName(relativePath);
      SolidityParser parser = Utils.returnParserFromParsedFile(cs);
      checkVisitor.setRuleContext(testRuleContext);
      checkVisitor.visit(parser.sourceUnit());
      if (reportMessage != null) {
        assertThat(TestRuleContext.issues).isNotEmpty();
        assertThat(TestRuleContext.issues).hasSize(1);
        assertThat(TestRuleContext.issues.get(0)).isEqualTo(reportMessage);
      }
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }
    testRuleContext.clearList();
  }

  public static void verifyIssue(IssuableVisitor check, String relativePath, String reportMessage) {
    verifyIssueOnFile(check, relativePath, reportMessage);
  }

  public static void verifyNoIssue(IssuableVisitor check, String relativePath) {
    verifyIssueOnFile(check, relativePath, null);
  }

  /*
   * class used to report for unit tests
   */

  private static class TestRuleContext implements RuleContext {
    protected static final List<String> issues = new ArrayList<>();

    @Override
    public void addIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
      /* no reason to implement for now */
    }

    @Override
    public void addIssue(Token start, Token stop, int offset, String reportMessage, String externalRuleKey) {
      /* no reason to implement for now */
    }

    @Override
    public void addIssueOnFile(String reportMessage, String externalRuleKey) {
      issues.add(reportMessage);
    }

    protected void clearList() {
      issues.clear();
    }

    @Override
    public void addIssue(ParserRuleContext ctx, String reportMessage, String externalRuleKey) {
      /* no reason to implement for now */
    }
  }
}
