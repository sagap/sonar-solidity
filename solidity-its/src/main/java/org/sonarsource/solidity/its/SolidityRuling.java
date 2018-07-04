package org.sonarsource.solidity.its;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class SolidityRuling {

  private static final Logger LOG = Loggers.get(SolidityRuling.class);

  private SolidityRuling() {
    // ... no reason for a public constructor
  }

  private static final String DIR = "solidity-test-sources/src/";
  private static final String[] PROJECTS_TO_ANALYZE = {
    "ethereum-api",
    "Random-Files"
  };

  public static String[] getProjects() {
    return PROJECTS_TO_ANALYZE;
  }

  public static void collectSolidityFiles() {
    Arrays.asList(getProjects())
      .stream()
      .map(x -> String.format("%s%s", DIR, x))
      .forEach(path -> {
        try {
          Files.find(Paths.get(path),
            Integer.MAX_VALUE,
            (filePath, fileAttr) -> fileAttr.isRegularFile())
            .filter(file -> file.getFileName().toString().endsWith(".sol"))
            .forEach(file -> analyzeFile(file.toFile()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
  }

  private static void analyzeFile(File file) {
    LOG.debug("Analyze File: " + file);

  }

}
