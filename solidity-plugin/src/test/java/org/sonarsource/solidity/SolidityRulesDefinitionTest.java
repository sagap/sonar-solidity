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
    assertThat(rulesDefinition.rulesDefinitionFilePath()).isNotNull();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    context.createRepository(Solidity.KEY, "solidity");
    System.out.println(context.repositories() + " " + Solidity.KEY + " , " + SolidityRulesDefinition.REPO_KEY);
    RulesDefinition.Repository repository = context.repository(SolidityRulesDefinition.REPO_KEY);
    assertThat(repository.name()).isEqualTo(Solidity.KEY);
    assertThat(repository.language()).isEqualTo("solidity");
    assertThat(repository.rules()).hasSize(3);
    List<Rule> activated = repository.rules().stream().filter(x -> x.status().name().equals("READY")).collect(Collectors.toList());
    assertThat(activated).isNotEmpty();
    assertThat(activated.size()).isEqualTo(1);
  }
}
