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
      .add(EmptyFunctionCheck.class)
      .add(EmptyFileCheck.class)
      .add(InvertedBooleanCheck.class)
      .add(CurlyBraceCheck.class)
      .add(ConstructorVisibilityCheck.class)
      .add(DeprecatedConstructorCheck.class)
      .add(CognitiveComplexityCheck.class)
      .add(ParameterAndFunctionsNamingCheck.class)
      .add(PragmaExistCheck.class)
      .add(DeprecatedSuicideCheck.class)
      .add(AvoidTxOriginCheck.class)
      .add(AvoidSha3Check.class)
      .add(AccessRestrictionPatternCheck.class)
      .add(GuardCheckPatternCheck.class)
      .add(StringEqualityComparisonCheck.class)
      .add(OraclePatternCheck.class)
      .add(TransferEtherSecurelyCheck.class)
      .add(RandomnessCheck.class)
      .add(BytesLowerGasCheck.class)
      .add(CheckEffectsCheck.class)
      .add(TightVariablePackingCheck.class)
      .build();
  }
}
