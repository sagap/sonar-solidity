package org.sonarsource.solidity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInActiveRule;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInQualityProfile;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.Context;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityProfileTest {

  @Test
  public void test() {
    Context context = new Context();

    SolidityProfile definition = new SolidityProfile();
    definition.define(context);

    BuiltInQualityProfile profile = context.profile(Solidity.KEY, SolidityProfile.SONAR_WAY_PROFILE);

    assertThat(profile).isNotNull();
    List<BuiltInActiveRule> activeRules = profile.rules();
    Set<String> ruleKeys = activeRules.stream().map(BuiltInActiveRule::ruleKey).collect(Collectors.toSet());

    assertThat(activeRules.size()).isGreaterThan(0);
    assertThat(ruleKeys).contains("ExternalRule1");
    // assertThat(ruleKeys).excludes("S4173");
  }
}
