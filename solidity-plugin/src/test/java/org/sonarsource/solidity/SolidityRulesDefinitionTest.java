package org.sonarsource.solidity;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityRulesDefinitionTest {

  @Test
  public void foo() {
    SolidityRulesDefinition rulesDefinition = new SolidityRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    context.createRepository(Solidity.KEY, "solidity");
    RulesDefinition.Repository repository = context.repository(SolidityRulesDefinition.REPO_KEY);
    assertThat(repository.name()).isEqualTo(SolidityRulesDefinition.REPO_NAME);
    assertThat(repository.language()).isEqualTo("solidity");
    // assertThat(repository.rules()).hasSize(3);
    assertThat(repository.rules()).hasSize(18);
    List<Rule> activated = repository.rules().stream().filter(x -> x.status().name().equals("READY")).collect(Collectors.toList());
    assertThat(activated).isNotEmpty();
    assertThat(activated.size()).isEqualTo(18);
  }
}
