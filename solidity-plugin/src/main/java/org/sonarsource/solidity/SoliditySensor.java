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
import org.antlr.v4.runtime.RecognitionException;
import org.sonar.api.SonarProduct;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
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

  protected static final String REPORT_PATH_KEY = "sonar.solidity.reportPath";

  protected CognitiveComplexityVisitor cognitiveComplexity;

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
    for (InputFile file : files) {
      String lastAnalyzedFile = file.toString();
      if (inSonarQube(context)) {
        try {
          LOG.debug("Analyzing: " + lastAnalyzedFile);
          SolidityParser parser = Utils.returnParserUnitFromParsedFile(file.contents());
          getSyntaxHighlighting(parser, context, file).save();
          saveFileMeasures(context, computeMeasures(parser, fileLinesContextFactory.createFor(file), file), file);
        } catch (IOException e) {
          LOG.debug(e.getMessage(), e);
        }
      } else {
        LOG.debug(lastAnalyzedFile);
      }
    }
  }

  private FileMeasures computeMeasures(SolidityParser parser, FileLinesContext fileLinesContext, InputFile file) throws RecognitionException, IOException {
    MetricsVisitor metricsVisitor = new MetricsVisitor(parser);

    cognitiveComplexity = new CognitiveComplexityVisitor(Utils.returnParserUnitFromParsedFile(file.contents()).sourceUnit());
    int totalComplexity = cognitiveComplexity.functionsComplexity.values().stream().mapToInt(Integer::intValue).sum();
    metricsVisitor.fileMeasures.setFileCognitiveComplexity(totalComplexity);
    return metricsVisitor.fileMeasures;
  }

  public NewHighlighting getSyntaxHighlighting(SolidityParser parser, SensorContext context, InputFile inputFile) {
    NewHighlighting highlighting = context.newHighlighting().onFile(inputFile);
    SyntaxHighlightingVisitor visitor = new SyntaxHighlightingVisitor(highlighting);
    visitor.visitTokens(parser.getTokenStream());
    visitor.highlightComments(parser);
    return highlighting;
  }

  private static void saveFileMeasures(SensorContext context, FileMeasures fileMeasures, InputFile inputFile) {
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getLinesOfCodeNumber()).forMetric(CoreMetrics.NCLOC).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getCommentLinesNumber()).forMetric(CoreMetrics.COMMENT_LINES).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getContractNumber()).forMetric(CoreMetrics.CLASSES).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getFunctionNumber()).forMetric(CoreMetrics.FUNCTIONS).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getStatementNumber()).forMetric(CoreMetrics.STATEMENTS).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getFileCognitiveComplexity()).forMetric(CoreMetrics.COGNITIVE_COMPLEXITY).save();
    context.<Integer>newMeasure().on(inputFile).withValue(fileMeasures.getFileComplexity()).forMetric(CoreMetrics.COMPLEXITY).save();
  }

  private static boolean inSonarQube(SensorContext context) {
    return !context.getSonarQubeVersion().isGreaterThanOrEqual(SQ_VERSION)
      || context.runtime().getProduct() != SonarProduct.SONARLINT;
  }
}
