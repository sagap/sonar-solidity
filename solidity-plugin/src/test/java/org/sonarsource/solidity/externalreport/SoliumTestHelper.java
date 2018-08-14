package org.sonarsource.solidity.externalreport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.ExternalIssue;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

public final class SoliumTestHelper {

  private SoliumTestHelper() {
  }

  public static List<ExternalIssue> executeSensor(Sensor sensor, SensorContextTester context) {
    sensor.execute(context);
    return new ArrayList<>(context.allExternalIssues());
  }

  public static SensorContextTester createContext(int majorVersion, int minorVersion) throws IOException {
    Path workDir = Files.createTempDirectory("soliditytemp");
    workDir.toFile().deleteOnExit();
    Path projectDir = Files.createTempDirectory("solidityproject");
    projectDir.toFile().deleteOnExit();
    SensorContextTester context = SensorContextTester.create(workDir);
    context.fileSystem().setWorkDir(workDir);
    Path filePath = projectDir.resolve("main.sol");
    InputFile mainInputFile = TestInputFileBuilder.create("module", projectDir.toFile(), filePath.toFile())
      .setCharset(StandardCharsets.UTF_8)
      .setLanguage("solidity")
      .setContents("pragma solidity 0.4.24;\n" +
        "\n" +
        "contract AccessRestrictionPattern {\n" +
        "\n" +
        "    address public owner = msg.sender;\n" +
        "    uint public lastOwnerChange = now;\n" +
        "    \n" +
        "    modifier onlyBy(address _account) {\n" +
        "        require(msg.sender == _account);\n" +
        "        _;\n" +
        "    }\n" +
        "    \n" +
        "    modifier onlyAfter(uint _time) {\n" +
        "        require(now >= _time);\n" +
        "        _;\n" +
        "    }\n" +
        "    \n" +
        "    modifier costs(uint _amount) {\n" +
        "        require(msg.value >= _amount);\n" +
        "        _;\n" +
        "        if (msg.value > _amount) {\n" +
        "            msg.sender.transfer(msg.value - _amount);\n" +
        "        }\n" +
        "    }\n" +
        "    \n" +
        "    function changeOwner(address _newOwner) public onlyBy(owner) {\n" +
        "        owner = _newOwner;\n" +
        "    }\n" +
        "    \n" +
        "    function buyContract() public payable onlyAfter(lastOwnerChange + 4 weeks) costs(1 ether) {\n" +
        "        owner = msg.sender;\n" +
        "        lastOwnerChange = now;\n" +
        "    }\n" +
        "}\n" +
        "")
      .setType(InputFile.Type.MAIN)
      .build();
    context.fileSystem().add(mainInputFile);
    context.setRuntime(SonarRuntimeImpl.forSonarQube(Version.create(majorVersion, minorVersion), SonarQubeSide.SERVER));
    return context;
  }
}
