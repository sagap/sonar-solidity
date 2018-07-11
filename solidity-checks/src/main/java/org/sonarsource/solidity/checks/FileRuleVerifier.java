package org.sonarsource.solidity.checks;

import com.sonarsource.checks.verifier.SingleFileVerifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.Utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class FileRuleVerifier {

  private static final Logger LOG = Loggers.get(FileRuleVerifier.class);

  private static void verifyIssueOnFile(IssuableVisitor checkVisitor, String relativePath, String reportMessage) {
    File file = new File(relativePath);
    SingleFileVerifier verifier = SingleFileVerifier.create(file.toPath(), UTF_8);
    CharStream cs;
    try {
      cs = CharStreams.fromFileName(relativePath);
      SolidityParser parser = Utils.returnParserFromParsedFile(cs);
      TestRuleContext testRuleContext = new TestRuleContext(verifier);
      checkVisitor.setRuleContext(testRuleContext);
      checkVisitor.visit(parser.sourceUnit());
      if (reportMessage != null) {
        assertThat(testRuleContext.issues).isNotEmpty();
        assertThat(testRuleContext.issues).hasSize(1);
        assertThat(testRuleContext.issues.get(0)).isEqualTo(reportMessage);
      }
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }

  }

  public FileRuleVerifier(IssuableVisitor check, String relativePath, String reportMessage) {
    verifyIssueOnFile(check, relativePath, reportMessage);
  }

  public static void verifyNoIssue(IssuableVisitor check, String relativePath) {
    verifyIssueOnFile(check, relativePath, null);
  }

  /*
   * class used to report for unit tests
   */

  private static class TestRuleContext implements RuleContext {

    SingleFileVerifier verifier;
    public List<String> issues;

    public TestRuleContext(SingleFileVerifier verifier) {
      this.verifier = verifier;
      issues = new ArrayList<>();
    }

    @Override
    public void addIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
    }

    @Override
    public void addIssue(Token start, Token stop, int offset, String reportMessage, String externalRuleKey) {
    }

    @Override
    public void addIssueOnFile(String reportMessage, String externalRuleKey) {
      // verifier.reportIssue(reportMessage).onFile();
      issues.add(reportMessage);
    }
  }
}
