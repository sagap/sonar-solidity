package org.sonarsource.solidity.frontend;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Test;
import org.sonarsource.solidity.frontend.SolidityParser.BlockContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ContractPartContext;
import org.sonarsource.solidity.frontend.SolidityParser.EnumDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ExpressionContext;
import org.sonarsource.solidity.frontend.SolidityParser.FunctionDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.IdentifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.IfStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.ImportDirectiveContext;
import org.sonarsource.solidity.frontend.SolidityParser.InheritanceSpecifierContext;
import org.sonarsource.solidity.frontend.SolidityParser.ModifierDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.ParameterListContext;
import org.sonarsource.solidity.frontend.SolidityParser.PragmaDirectiveContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.frontend.SolidityParser.StateVariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StructDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.WhileStatementContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityParserTest {

  @Test
  public void test_parsing() {
    String file = "pragma solidity ^0.4.22;\n" +
      "\n" +
      "/// @title Voting with delegation.\n" +
      "contract Ballot {\n" +
      " // This declares a new complex type which will\n" +
      " // be used for variables later.\n" +
      " // It will represent a single voter.\n" +
      " struct Voter {\n" +
      " uint weight; // weight is accumulated by delegation\n" +
      " bool voted; // if true, that person already voted\n" +
      " address delegate; // person delegated to\n" +
      " uint vote; // index of the voted proposal\n" +
      " }\n" +
      "}";
    SolidityParser parser = returnSourceUnitFromParsedFile(file);
    SourceUnitContext suc = parser.sourceUnit();
    PragmaDirectiveContext pdc = suc.pragmaDirective().get(0);
    assertThat(pdc).isNotNull();
    ContractDefinitionContext c = suc.contractDefinition().get(0);
    assertThat(c).isNotNull();

    // in this source unit ctx there is no import directive
    assertThat(suc.importDirective()).isEmpty();

    // now check that contract definition is well parsed
    assertThat(c.inheritanceSpecifier()).isEmpty();

    ContractPartContext cp = c.contractPart().get(0);
    assertThat(cp).isNotNull();

    // now check for contractpart
    StructDefinitionContext sdc = cp.structDefinition();
    assertThat(sdc).isNotNull();

    IdentifierContext ic = sdc.identifier();
    assertThat(ic).isNotNull();

    List<VariableDeclarationContext> vdcList = sdc.variableDeclaration();
    assertThat(vdcList).hasSize(4);
  }

  @Test
  public void test_parsing_file() throws IOException {
    CharStream cs = CharStreams.fromFileName("src/test/resources/test1.sol");
    SolidityParser parser = returnSourceUnitFromParsedFile(cs);
    SourceUnitContext suc = parser.sourceUnit();
    assertThat(suc).isNotNull();

    ContractDefinitionContext cdc = suc.contractDefinition().get(0);
    assertThat(cdc).isNotNull();

    List<ContractPartContext> cpcList = cdc.contractPart();

    List<FunctionDefinitionContext> funList = cpcList.stream()
      .map(ContractPartContext::functionDefinition)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    assertThat(funList).hasSize(3);

    List<StateVariableDeclarationContext> vars = cpcList.stream()
      .map(ContractPartContext::stateVariableDeclaration)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    assertThat(vars).hasSize(3);

    BlockContext blckCtx = funList.get(0).block();
    List<StatementContext> stmt = blckCtx.statement();
    assertThat(stmt.get(0).simpleStatement()).isNotNull();
    assertThat(stmt.get(1).simpleStatement()).isNotNull();
    assertThat(stmt.get(2).simpleStatement()).isNotNull();

    WhileStatementContext whileStmt = stmt.get(3).whileStatement();
    assertThat(whileStmt).isNotNull();
    assertThat(whileStmt.statement().simpleStatement()).isNotNull();

    blckCtx = funList.get(1).block();
    stmt = blckCtx.statement();
    assertThat(stmt.get(0).returnStatement()).isNotNull();

    blckCtx = funList.get(2).block();
    stmt = blckCtx.statement();
    IfStatementContext ifStmt = stmt.get(0).ifStatement();
    assertThat(ifStmt).isNotNull();
    blckCtx = ifStmt.statement().get(0).block();
    assertThat(blckCtx.statement().get(0).simpleStatement()).isNotNull();

    assertThat(parser.comments).hasSize(3);
  }

  @Test
  public void test2() {
    String file = "contract Coin {\n" +
      "    // The keyword \"public\" makes those variables\n" +
      "    // readable from outside.\n" +
      "    address public minter;\n" +
      "    mapping (address => uint) public balances;\n" +
      "\n" +
      "    // Events allow light clients to react on\n" +
      "    // changes efficiently.\n" +
      "    event Sent(address from, address to, uint amount);}";

    SolidityParser parser = returnSourceUnitFromParsedFile(file);
    SourceUnitContext suc = parser.sourceUnit();
    ContractDefinitionContext cdc = suc.contractDefinition().get(0);
    assertThat(cdc).isNotNull();

    List<ContractPartContext> cpcList = cdc.contractPart();
    assertThat(cpcList).hasSize(3);

    assertThat(cpcList.get(0).stateVariableDeclaration()).isNotNull();
    assertThat(cpcList.get(1).stateVariableDeclaration()).isNotNull();
    assertThat(cpcList.get(2).eventDefinition()).isNotNull();
  }

  @Test
  public void test3() {
    String file = "contract c {\n" +
      "    modifier mod1(address a) { if (msg.sender == a) _; }\n" +
      "    modifier mod2 { if (msg.sender == 2) _; }\n" +
      "    function f() mod1(7) mod2 { }\n" +
      "    function f2(){\n" +
      "        var (a,b,c) = g();\n" +
      "    }\n" +
      "    function g() returns (uint, uint, uint) {}\n" +
      "}\n" +
      "";

    SolidityParser parser = returnSourceUnitFromParsedFile(file);
    SourceUnitContext suc = parser.sourceUnit();
    ContractDefinitionContext cdc = suc.contractDefinition().get(0);
    assertThat(cdc.identifier().getText()).isEqualTo("c");

    List<ContractPartContext> cpcList = cdc.contractPart();
    assertThat(cpcList).hasSize(5);

    assertThat(cpcList.get(0).modifierDefinition()).isNotNull();
    ModifierDefinitionContext mod = cpcList.get(0).modifierDefinition();
    ParameterListContext paramList = mod.parameterList();
    assertThat(paramList.parameter(0).identifier().getText()).isEqualTo("a");
    IfStatementContext ifStmt = mod.block().statement().get(0).ifStatement();
    ExpressionContext exprCtx = ifStmt.expression();
    TerminalNode node = (TerminalNode) exprCtx.getChild(1);
    assertThat(node.getText()).isEqualTo("==");
    assertThat(cpcList.get(2).functionDefinition()).isNotNull();
  }

  @Test
  public void test_import() {
    String file = "import \"./abc.sol\";";
    SolidityParser parser = returnSourceUnitFromParsedFile(file);
    SourceUnitContext suc = parser.sourceUnit();
    ImportDirectiveContext idCtx = suc.importDirective().get(0);
    assertThat(idCtx).isNotNull();
    assertThat(Utils.trimQuotes(idCtx.StringLiteral().getText())).isEqualTo("./abc.sol");
  }

  @Test
  public void test_inheritance() {
    String file = "contract base {\n" +
      "}\n" +
      "contract derived is base {\n" +
      "enum foo { }\n" +
      "}";
    SolidityParser parser = returnSourceUnitFromParsedFile(file);
    SourceUnitContext suc = parser.sourceUnit();
    ContractDefinitionContext cdCtx = suc.contractDefinition(1);
    assertThat(cdCtx.inheritanceSpecifier().get(0).getText()).isEqualTo("base");
    InheritanceSpecifierContext isCtx = cdCtx.inheritanceSpecifier(0);
    assertThat(isCtx.userDefinedTypeName().getText()).isEqualTo("base");
    assertThat(isCtx.expression(0)).isNull();
    assertThat(isCtx.expression()).isEmpty();
    assertThat(cdCtx.identifier().getText()).isEqualTo("derived");
    ContractPartContext cpCtx = cdCtx.contractPart(0);
    EnumDefinitionContext edCtx = cpCtx.enumDefinition();
    assertThat(edCtx).isNotNull();
    assertThat(edCtx.identifier().getText()).isEqualTo("foo");
  }

  @Test
  public void test_foo() {
    String file = "contract base {\n" +
      "    // ... test\n" +
      "}";
    SolidityParser parser = returnSourceUnitFromParsedFile(file);
    SourceUnitContext suc = parser.sourceUnit();
    assertThat(parser.comments).hasSize(1);
  }

  // ... source unit context is the root of the parse tree
  private static SolidityParser returnSourceUnitFromParsedFile(String file) {
    CharStream cs = CharStreams.fromString(file);
    SolidityLexer sl = new SolidityLexer(cs);
    // TokenStream tokens = new CommonTokenStream(sl);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokenStream);
    tokenStream.fill();
    parser.handleComments(tokenStream.getTokens());
    return parser;
  }

  private static SolidityParser returnSourceUnitFromParsedFile(CharStream cs) {
    SolidityLexer sl = new SolidityLexer(cs);
    // TokenStream tokens = new CommonTokenStream(sl);
    CommonTokenStream tokenStream = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokenStream);
    tokenStream.fill();
    parser.handleComments(tokenStream.getTokens());
    return parser;
  }
}
