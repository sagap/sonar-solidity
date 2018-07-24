package org.sonarsource.solidity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.duplications.internal.pmd.TokensLine;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityCpdTest {

  @Test
  public void test() {
    String filename = "test1.sol";
    SensorContextTester context = SensorContextTester.create(new File("src/test/resources").getAbsoluteFile());
    InputFile file = createInputFile(filename);
    context.fileSystem().add(file);
    try {
      SolidityCpd.addTokensCpd(context.newCpdTokens().onFile(file), file.contents());
      List<TokensLine> tokensLines = context.cpdTokens("module:" + filename);
      assertThat(tokensLines).hasSize(26);
      assertThat(tokensLines.get(0).getValue()).isEqualTo("contractTwoD{");
      assertThat(tokensLines.get(4).getValue()).isEqualTo("stringconstant_string=\"cryptopus.co Medium\";");
      assertThat(tokensLines.get(10).getValue()).isEqualTo("while(y<arraylength)");
    } catch (IOException e) {
      e.printStackTrace();
    }
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
