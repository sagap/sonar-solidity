package org.sonarsource.solidity;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;
import org.sonarsource.solidity.checks.CheckList;
import org.sonarsource.solidity.externalreport.AbstractExternalReportSensor;
import org.sonarsource.solidity.externalreport.SoliumReportSensor;

public class SolidityRulesDefinition implements RulesDefinition {

  protected static final String KEY = "solidity";
  protected static final String NAME = "Solidity";

  public static final String REPO_KEY = Solidity.KEY + "-" + KEY;
  protected static final String REPO_NAME = Solidity.KEY + "-" + NAME;

  private final boolean externalIssuesSupported;

  public SolidityRulesDefinition(boolean reportExternalIssues) {
    this.externalIssuesSupported = reportExternalIssues;
  }

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(SolidityRulesDefinition.REPO_KEY, Solidity.KEY)
      .setName(SolidityRulesDefinition.REPO_NAME);

    RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader("org/sonar/l10n/solidity/rules/solidity", SolidityProfile.SONAR_WAY_PROFILE_PATH);
    ruleMetadataLoader.addRulesByAnnotatedClass(repository, CheckList.returnChecks());

    repository.done();

    if (externalIssuesSupported) {
      AbstractExternalReportSensor.createExternalRuleRepository(context, SoliumReportSensor.LINTER_ID, SoliumReportSensor.LINTER_NAME);
    }
  }
}
