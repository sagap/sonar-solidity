package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;

public final class RuleKeyList {
  public static List<String> returnChecks() {
    return ImmutableList.<String>builder()
                        .add("1")
                        .build();
  };

}
