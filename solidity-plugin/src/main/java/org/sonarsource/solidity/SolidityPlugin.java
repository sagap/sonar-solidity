package org.sonarsource.solidity;

import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.utils.Version;
import org.sonarsource.solidity.externalreport.SoliumReportSensor;

/**
 * This class is the entry point for all extensions. It is referenced in pom.xml.
 */
public class SolidityPlugin implements Plugin {

  public static final String FILE_SUFFIXES_KEY = "sonar.solidity.file.suffixes";
  public static final String FILE_SUFFIXES_DEFAULT_VALUE = ".sol";
  private static final String EXTERNAL_LINTER_SUBCATEGORY = "Popular Rule Engines";

  @Override
  public void define(Context context) {

    boolean externalIssuesSupported = context.getSonarQubeVersion().isGreaterThanOrEqual(Version.create(7, 2));

    context.addExtension(Solidity.class);
    context.addExtension(new SolidityRulesDefinition(externalIssuesSupported));
    context.addExtension(SolidityProfile.class);
    context.addExtension(SoliditySensor.class);
    context.addExtension(SoliumReportSensor.class);

    context.addExtension(
      PropertyDefinition.builder(FILE_SUFFIXES_KEY)
        .defaultValue(FILE_SUFFIXES_DEFAULT_VALUE)
        .category("Solidity")
        .name("File Suffixes")
        .description("Comma-separated list of suffixes for files to analyze.")
        .onQualifiers(Qualifiers.PROJECT)
        .multiValues(true)
        .build());

    if (externalIssuesSupported) {
      context.addExtension(
        PropertyDefinition.builder(SoliumReportSensor.PROPERTY_KEY)
          .defaultValue("")
          .category("Solidity")
          .index(30)
          .name(" \"solium\" Report Files")
          .description("Paths to the files with \"solium\" issues.")
          .subCategory(EXTERNAL_LINTER_SUBCATEGORY)
          .onQualifiers(Qualifiers.PROJECT)
          .multiValues(true)
          .build());
    }
  }
}
