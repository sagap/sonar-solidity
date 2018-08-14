package org.sonarsource.solidity.externalreport;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class SoliumKeyUtils {
  private SoliumKeyUtils() {
    // Utils class
  }

  private static Set<String> rulesKeyset = ImmutableSet.<String>builder()
    .add("imports-on-top")
    .add("variable-declarations")
    .add("array-declarations")
    .add("operator-whitespace")
    .add("conditionals-whitespace")
    .add("comma-whitespace")
    .add("semicolon-whitespace")
    .add("function-whitespace")
    .add("lbrace")
    .add("mixedcase")
    .add("camelcase")
    .add("uppercase")
    .add("no-with")
    .add("no-empty-blocks")
    .add("no-unused-vars")
    .add("double-quotes")
    .add("quotes")
    .add("blank-lines")
    .add("indentation")
    .add("arg-overflow")
    .add("whitespace")
    .add("deprecated-suicide")
    .add("pragma-on-top")
    .add("function-order")
    .add("emit")
    .add("no-constant")
    .add("value-in-payable")
    .add("no-experimental")
    .add("max-len")
    .add("error-reason")
    .add("visibility-first")
    .add("linebreak-style")
    .add("security/no-throw")
    .add("security/no-tx-origin")
    .add("security/enforce-explicit-visibility")
    .add("security/no-block-members")
    .add("security/no-call-value")
    .add("security/no-assign-params")
    .add("security/no-fixed")
    .add("security/no-inline-assembly")
    .add("security/no-low-level-calls")
    .add("security/no-modify-for-iter-var")
    .add("security/no-send")
    .add("security/no-sha3")
    .add("security/no-unreachable-code")
    .build();

  public static boolean keyExists(String key) {
    return rulesKeyset.contains(key);
  }
}
