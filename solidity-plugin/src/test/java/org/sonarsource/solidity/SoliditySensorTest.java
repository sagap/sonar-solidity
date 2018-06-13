package org.sonarsource.solidity;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.Test;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SoliditySensorTest {

  private static final Version SONARLINT_DETECTABLE_VERSION = Version.create(6, 7);
  private static final SonarRuntime SONARQUBE_6_7 = SonarRuntimeImpl.forSonarQube(Version.create(6, 7), SonarQubeSide.SCANNER);
  private static final SonarRuntime SONARLINT_RUNTIME = SonarRuntimeImpl.forSonarLint(SONARLINT_DETECTABLE_VERSION);

  private Path workDir;
  private Path projectDir;
  private SensorContextTester sensorContext = SensorContextTester.create(new File("src/test/resources").getAbsoluteFile());

  private FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);

  private SoliditySensor createSensor() {
    return new SoliditySensor(createFileLinesContextFactory());
  }

  private FileLinesContextFactory createFileLinesContextFactory() {
    FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
    FileLinesContext fileLinesContext = mock(FileLinesContext.class);
    when(fileLinesContextFactory.createFor(any(InputFile.class))).thenReturn(fileLinesContext);
    return fileLinesContextFactory;
  }

  @Test
  public void test_description() {
    DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
    SoliditySensor sensor = new SoliditySensor(createFileLinesContextFactory());
    sensor.describe(descriptor);
    assertThat(descriptor.name()).isEqualTo("SonarSolidity");
    assertThat(descriptor.languages()).containsOnly("solidity");
  }

  @Test
  public void test_file() {
    String filename = "test1.sol";
    SoliditySensor sensor = new SoliditySensor(createFileLinesContextFactory());
    analyseSingleFile(sensor, filename);
  }

  private void analyseSingleFile(SoliditySensor sensor, String filename) {
    sensorContext.fileSystem().add(createInputFile(filename));
    sensor.execute(sensorContext);
  }

  private InputFile createInputFile(String filename) {
    try {
      return TestInputFileBuilder.create("module", filename)
        .setModuleBaseDir(getModuleBaseDir().toPath())
        .setCharset(StandardCharsets.UTF_8)
        .setLanguage(Solidity.KEY)
        .initMetadata(new String(java.nio.file.Files.readAllBytes(new File("src/test/resources/" + filename).toPath()), StandardCharsets.UTF_8)).build();
    } catch (java.io.IOException e) {
      throw new IllegalStateException("File Not Found!", e);
    }
  }

  public static File getModuleBaseDir() {
    return new File("src/test/resources");
  }
}
