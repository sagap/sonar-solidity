package org.sonarsource.solidity;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public final class Solidity extends AbstractLanguage {

  public static final String NAME = "Solidity";
  public static final String KEY = "solidity";

  private final Configuration config;

  /**
   * 
   * Construct Solidity Language
   */

  public Solidity(Configuration config) {
    super(KEY, NAME);
    this.config = config;
  }

  @Override
  public String[] getFileSuffixes() {
    return config.getStringArray(SolidityPlugin.FILE_SUFFIXES_KEY);
  }

  public boolean hasValidSuffixes(String fileName) {
    String pathLowerCase = StringUtils.lowerCase(fileName);
    for (String suffix : getFileSuffixes()) {
      if (pathLowerCase.endsWith("." + StringUtils.lowerCase(suffix))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Solidity other = (Solidity) o;
    return this.config.equals(other.config);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
