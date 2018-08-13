package org.sonarsource.solidity;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityPluginTest {

  private SolidityPlugin plugin;

  @Before
  public void setUp() {
    this.plugin = new SolidityPlugin();
  }

  @Test
  public void count_plugin_extensions7_2() {
    Plugin.Context context = setupContext(SonarRuntimeImpl.forSonarQube(Version.create(7, 2), SonarQubeSide.SERVER));
    assertThat(context.getExtensions()).as("Number of extensions for SQ 7.2").hasSize(7);
  }

  @Test
  public void count_plugin_extensions() {
    Plugin.Context context = setupContext(SonarRuntimeImpl.forSonarQube(Version.create(7, 1), SonarQubeSide.SERVER));
    assertThat(context.getExtensions()).as("Number of extensions for SQ 7.1").hasSize(6);
  }

  @Test
  public void count_plugin_extensions_sq6_7() {
    Plugin.Context context = setupContext(SonarRuntimeImpl.forSonarQube(Version.create(6, 7), SonarQubeSide.SERVER));
    assertThat(context.getExtensions()).as("Number of extensions for SQ 6.7").hasSize(6);
  }

  private Plugin.Context setupContext(SonarRuntime runtime) {
    Plugin.Context context = new Plugin.Context(runtime);
    new SolidityPlugin().define(context);
    return context;
  }
}
