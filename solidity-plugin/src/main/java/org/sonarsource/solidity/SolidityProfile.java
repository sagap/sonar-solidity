package org.sonarsource.solidity;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonarsource.analyzer.commons.BuiltInQualityProfileJsonLoader;

public class SolidityProfile implements BuiltInQualityProfilesDefinition {

  public static final String SONAR_WAY_PROFILE = "Sonar way";
  public static final String SONAR_WAY_PROFILE_PATH = "org/sonar/l10n/solidity/rules/solidity/Sonar_way_profile.json";

  @Override
  public void define(Context context) {
    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(SONAR_WAY_PROFILE, Solidity.KEY);
    BuiltInQualityProfileJsonLoader.load(profile, SolidityRulesDefinition.REPO_KEY, SONAR_WAY_PROFILE_PATH);

    profile.setDefault(true);
    profile.done();
  }
}
