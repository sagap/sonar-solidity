/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
