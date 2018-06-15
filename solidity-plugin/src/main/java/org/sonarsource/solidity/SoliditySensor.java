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

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.sonar.api.SonarProduct;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.utils.Version;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.solidity.frontend.SolidityParser;
import org.sonarsource.solidity.frontend.Utils;

public class SoliditySensor implements Sensor {

  private static final Logger LOG = Loggers.get(SoliditySensor.class);

  private final FileLinesContextFactory fileLinesContextFactory;
  public static final Version SQ_VERSION = Version.create(6, 7);
  private NewHighlighting highlighting;

  public static final ImmutableList<String> KEYWORDS = ImmutableList.<String>builder()
    .add(SolidityKeywords.get()).build();

  public static final ImmutableList<String> KEYWORD_TYPES = ImmutableList.<String>builder()
    .add(SolidityKeywords.getKeyowrdTypes()).build();

  public SoliditySensor(FileLinesContextFactory fileLinesContextFactory) {
    this.fileLinesContextFactory = fileLinesContextFactory;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .onlyOnLanguage(Solidity.KEY)
      .name("SonarSolidity")
      .onlyOnFileType(Type.MAIN);
  }

  @Override
  public void execute(SensorContext context) {
    FileSystem fileSystem = context.fileSystem();

    FilePredicate mainFilePredicate = fileSystem.predicates().and(
      fileSystem.predicates().hasType(InputFile.Type.MAIN),
      fileSystem.predicates().hasLanguage(Solidity.KEY));

    List<InputFile> inputFiles = new ArrayList<>();
    fileSystem.inputFiles(mainFilePredicate).forEach(inputFiles::add);
    analyzeFiles(context, inputFiles);
  }

  public void analyzeFiles(SensorContext context, List<InputFile> files) {
    String lastAnalyzedFile = "no file analyzed";
    for (InputFile file : files) {
      lastAnalyzedFile = file.toString();
      if (inSonarQube(context)) {
        System.out.println("Analyzing: " + lastAnalyzedFile);
        getSyntaxHighlighting(context, file).save();
      }
    }
  }

  public NewHighlighting getSyntaxHighlighting(SensorContext context, InputFile inputFile) {
    this.highlighting = context.newHighlighting().onFile(inputFile);
    try {
      SolidityParser parser = Utils.returnParserUnitFromParsedFile(inputFile.contents());
      for (Token t : parser.comments) {
        if (t.getType() == 118) {
          highlightComment(t);
        } else { // TODO Structured Comments
        }
      }
      MyVisitor visitor = new MyVisitor(this.highlighting);
      visitor.visitTokens(parser.getTokenStream());
    } catch (IOException e) {
      LOG.error(e.toString());
    }
    return this.highlighting;
  }

  private void highlightComment(Token token) {
    this.highlighting.highlight(token.getLine(), token.getCharPositionInLine(), token.getLine(), (token.getCharPositionInLine() + token.getText().length()), TypeOfText.COMMENT);
  }

  private static boolean inSonarQube(SensorContext context) {
    return !context.getSonarQubeVersion().isGreaterThanOrEqual(SQ_VERSION)
      || context.runtime().getProduct() != SonarProduct.SONARLINT;
  }
}
