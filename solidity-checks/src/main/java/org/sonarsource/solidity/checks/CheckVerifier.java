package org.sonarsource.solidity.checks;

import com.sonarsource.checks.verifier.SingleFileVerifier;
import java.io.File;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.frontend.SolidityLexer;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.Utils;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CheckVerifier {

  private static final Logger LOG = Loggers.get(CheckVerifier.class);
  private static final Integer SUFFIX_LENGTH = 2;

  public static ParseTreePatternMatcher eCheckVerifier(boolean treePattern, String relativePath) {
    CharStream cs;
    ParseTreePatternMatcher p = null;
    try {
      cs = CharStreams.fromFileName(relativePath);
      SolidityLexer sl = new SolidityLexer(cs);
      CommonTokenStream tokenStream = new CommonTokenStream(sl);
      SolidityParser parser = Utils.returnParserFromParsedFile(cs);
      p = new ParseTreePatternMatcher(sl, parser);
      return p;
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }
    return p;
  }

  private CheckVerifier(IssuableVisitor checkVisitor, String relativePath, boolean expectIssues) {
    File file = new File(relativePath);
    SingleFileVerifier verifier = SingleFileVerifier.create(file.toPath(), UTF_8);
    CharStream cs;
    try {
      cs = CharStreams.fromFileName(relativePath);
      SolidityParser parser = Utils.returnParserFromParsedFile(cs);
      TestRuleContext testRuleContext = new TestRuleContext(verifier);
      checkVisitor.setRuleContext(testRuleContext);
      checkVisitor.visit(parser.sourceUnit());

      parser.comments.stream()
        .forEach(x -> {
          int line = x.getLine();
          int col = x.getCharPositionInLine();
          String val = x.getText();
          int suffixLength = SUFFIX_LENGTH;
          verifier.addComment(line, col, val, suffixLength, 0);
        });
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }
    if (expectIssues) {
      verifier.assertOneOrMoreIssues();
    } else {
      verifier.assertNoIssues();
    }
  }

  public CheckVerifier(IssuableVisitor check, String relativePath) {
    this(check, relativePath, true);
  }

  public static void verifyNoIssue(IssuableVisitor check, String relativePath) {
    new CheckVerifier(check, relativePath, false);
  }

  public static boolean verifyIssueOnFile(IssuableVisitor checkVisitor, String relativePath) {
    File file = new File(relativePath); ///// TODO fix architecture
    SingleFileVerifier verifier = SingleFileVerifier.create(file.toPath(), UTF_8);
    CharStream cs;
    try {
      cs = CharStreams.fromFileName(relativePath);
      SolidityParser parser = Utils.returnParserFromParsedFile(cs);
      TestRuleContext testRuleContext = new TestRuleContext(verifier);
      checkVisitor.setRuleContext(testRuleContext);
      checkVisitor.visit(parser.sourceUnit());
      if (testRuleContext.issueOnFile) {
        return true;
      }
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }
    return false;
  }

  /*
   * class used to report for unit tests
   */

  private static class TestRuleContext implements RuleContext {

    SingleFileVerifier verifier;
    protected boolean issueOnFile = false;

    public TestRuleContext(SingleFileVerifier verifier) {
      this.verifier = verifier;
    }

    @Override
    public void addIssue(Token start, Token stop, String reportMessage, String externalRuleKey) {
      int startColumn = start.getCharPositionInLine();
      if (startColumn == 0)
        startColumn = 1;
      int endColumn = stop.getCharPositionInLine();
      if (endColumn == 0)
        endColumn = 1;

      verifier.reportIssue(reportMessage).onRange(start.getLine(), startColumn, stop.getLine(), endColumn);
    }

    @Override
    public void addIssue(Token start, Token stop, int offset, String reportMessage, String externalRuleKey) {
      int startColumn = start.getCharPositionInLine();
      if (startColumn == 0)
        startColumn = 1;

      verifier.reportIssue(reportMessage).onRange(start.getLine(), startColumn, start.getLine(), stop.getCharPositionInLine() + offset);
    }

    @Override
    public void addIssueOnFile(String reportMessage, String externalRuleKey) {
      verifier.reportIssue(reportMessage).onFile();
      issueOnFile = true;
    }
  }
}
