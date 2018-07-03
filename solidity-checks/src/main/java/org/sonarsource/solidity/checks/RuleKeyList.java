package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;

public final class RuleKeyList {

  private RuleKeyList() {
  }

  public static List<String> returnChecks() {
    return ImmutableList.<String>builder()
      .add("ExternalRule1")
      .build();
  }
}
