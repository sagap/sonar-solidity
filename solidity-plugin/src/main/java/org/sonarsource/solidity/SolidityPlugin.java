/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.solidity;

import org.sonar.api.Plugin;
import org.sonarsource.solidity.languages.Solidity;
import org.sonarsource.solidity.languages.SolidityQualityProfile;
import org.sonarsource.solidity.rules.SolidityRulesDefinition;
import org.sonarsource.solidity.settings.SolidityLanguageProperties;
import org.sonarsource.solidity.settings.SoliditySensor;

/**
 * This class is the entry point for all extensions. It is referenced in pom.xml.
 */
public class SolidityPlugin implements Plugin {

  @Override
  public void define(Context context) {

    // language
    context.addExtension(Solidity.class);

    // sensor
    context.addExtension(SoliditySensor.class);

    // rules
    context.addExtension(SolidityRulesDefinition.class);

    // hooks
    // http://docs.sonarqube.org/display/DEV/Adding+Hooks
    // context.addExtensions(DisplayIssuesInScanner.class, DisplayQualityGateStatus.class);

    context.addExtension(SolidityQualityProfile.class);
    context.addExtension(SolidityLanguageProperties.getProperties());

    // // tutorial on measures
    // context
    // .addExtensions(ExampleMetrics.class, SetSizeOnFilesSensor.class, ComputeSizeAverage.class, ComputeSizeRating.class);
    //
    // // tutorial on rules
    // context.addExtension(FooLintIssuesLoaderSensor.class);

    // web extensions
    // context.addExtension(MyPluginPageDefinition.class);

    // context.addExtensions(asList(
    // PropertyDefinition.builder("sonar.foo.file.suffixes")
    // .name("Suffixes FooLint")
    // .description("Suffixes supported by FooLint")
    // .category("FooLint")
    // .defaultValue("")
    // .build()));
  }
}
