package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;

public final class CheckList {

  private CheckList() {
  }

  public static List<Class> returnChecks() {
    return ImmutableList.<Class>builder()
      .add(ContractNotEmptyCheck.class)
      .add(LatestVersionCheck.class)
      .build();
  }
}
