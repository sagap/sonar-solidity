package org.sonarsource.solidity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public final class UtilsSensor {

  private UtilsSensor() {
  }

  private static final Logger LOG = Loggers.get(UtilsSensor.class);

  public static int emptyLines(InputFile file) {
    String line;
    int emptyLinesCounter = 0;
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(file.inputStream()));
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() == 0) {
          emptyLinesCounter++;
        }
      }
    } catch (IOException e) {
      LOG.debug(e.getMessage(), e);
    }
    // +1 for the last line
    return emptyLinesCounter;
  }
}
