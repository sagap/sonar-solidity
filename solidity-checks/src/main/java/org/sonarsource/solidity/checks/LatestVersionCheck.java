package org.sonarsource.solidity.checks;

import java.util.Arrays;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.check.Rule;
import org.sonarsource.solidity.frontend.SolidityParser.PragmaDirectiveContext;

@Rule(key = LatestVersionCheck.RULE_KEY)
public class LatestVersionCheck extends IssuableVisitor {

  public static final String RULE_KEY = "ExternalRule1";

  private static final String[] LATEST_VERSION = "0.4.24".split("\\.");
  private static int[] latestVersion = null;
  static {
    latestVersion = Arrays.stream(LATEST_VERSION).mapToInt(Integer::parseInt).toArray();
  }

  @Override
  public ParseTree visitPragmaDirective(PragmaDirectiveContext ctx) {
    if (!isLatestVersion(ctx.pragmaValue().getText())) {
      ruleContext().addIssue(ctx.getStart(), ctx.getStop(), "Not the latest version of solidity", RULE_KEY);
    }
    return super.visitPragmaDirective(ctx);
  }

  private static boolean isLatestVersion(String version) {
    int[] currentVersion = Arrays.stream(version.substring(1).split("\\.")).mapToInt(Integer::parseInt).toArray();
    for (int i = 0; i < latestVersion.length; i++) {
      if (currentVersion[i] > latestVersion[i]) {
        return true;
      } else if (currentVersion[i] < latestVersion[i]) {
        return false;
      }
    }
    return true;
  }
}
