package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class CheckList {
  public static List<Class> returnChecks() {
    return ImmutableList.<Class>builder()
      .add(LatestVersionCheck.class)
      .build();
  }
}
