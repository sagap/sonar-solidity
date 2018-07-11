package org.sonarsource.solidity.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;

public final class RuleKeyList {

  private RuleKeyList() {
  }

  public static List<String> returnChecks() {
    return ImmutableList.<String>builder()
      .add("ExternalRule1")
      .add("ExternalRule2")
      .add("ExternalRule3")
      .add("ExternalRule4")
      .add("ExternalRule5")
      .add("ExternalRule6")
      .add("ExternalRule7")
      .add("ExternalRule8")
      .add("ExternalRule9")
      .add("ExternalRule11")
      .add("ExternalRule12")
      .build();
  }
}
