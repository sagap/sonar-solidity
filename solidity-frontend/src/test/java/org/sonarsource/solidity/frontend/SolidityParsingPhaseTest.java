package org.sonarsource.solidity.frontend;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
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
import org.sonarsource.solidity.frontend.SolidityParser.ReturnStatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.frontend.SolidityParser.StateVariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.StatementContext;
import org.sonarsource.solidity.frontend.SolidityParser.StructDefinitionContext;
import org.sonarsource.solidity.frontend.SolidityParser.VariableDeclarationContext;
import org.sonarsource.solidity.frontend.SolidityParser.WhileStatementContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityParsingPhaseTest {

  @Test
  public void test_parsing() {
    String file = " pragma solidity ^0.4.22;\n" +
      "       /// @title Voting with delegation.   +\n" +
      "       contract Ballot {   \n" +
      "        // This declares a new complex type which will   +\n" +
      "        // be used for variables later.   +\n" +
      "        // It will represent a single voter.   +\n" +
      "        struct Voter {      // Noncompliant {{here}}   +\n" +
      "        uint weight; // weight is accumulated by delegation   +\n" +
      "        bool voted; // if true, that person already voted   +\n" +
      "        address delegate; // person delegated to   +\n" +
      "        uint vote; // index of the voted proposal   +\n" +
      "        }   \n" +
      "       bytes32 itta =  \"1\";  \n" +
      "        // ^^^^^^^  \n" +
      "       }";
    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(file);
    parser.setEmptyLines(0);
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
    assertThat(Utils.typeMatches(parser.getTokens().get(0), SolidityParser.StringLiteral)).isFalse();
    assertThat(Utils.typeMatches(parser.getTokens().get(36), SolidityParser.StringLiteral)).isTrue();
    List<VariableDeclarationContext> vdcList = sdc.variableDeclaration();
    assertThat(vdcList).hasSize(4);
    assertThat(parser.getEmptyLines()).isZero();
    assertThat(parser.getLinesOfComments()).isNotNull();
    assertThat(parser.getTokens()).isNotNull();
    assertThat(parser.comments.stream().filter(comment -> Utils.isCommentSignificant(comment.getText())).collect(Collectors.toList())).isNotNull();

  }

  @Test
  public void test_parsing_file() throws IOException {
    CharStream cs = CharStreams.fromFileName("src/test/resources/test1.sol");
    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(cs);
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

    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(file);
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

    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(file);
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
    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(file);
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
    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(file);
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
    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(file);
    assertThat(parser.comments).hasSize(1);
    assertThat(suc.getRuleIndex()).isEqualTo(0);
    assertThat(suc.EOF()).isNotNull();
  }

  @Test
  public void test_foo2() throws IOException {
    CharStream cs = CharStreams.fromFileName("src/test/resources/test_ternary.sol");
    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(cs);
    assertThat(suc).isNotNull();

    ContractDefinitionContext cdc = suc.contractDefinition().get(0);
    assertThat(cdc).isNotNull();

    List<ContractPartContext> cpList = cdc.contractPart();
    assertThat(cpList).isNotEmpty();

    FunctionDefinitionContext functionCtx = cpList.get(0).functionDefinition();
    assertThat(functionCtx).isNotNull();

    assertThat(functionCtx.modifierList()).isNotNull();
    List<StatementContext> stmtCtx = functionCtx.block().statement();
    ReturnStatementContext retStmt = stmtCtx.get(0).returnStatement();
    assertThat(retStmt).isNotNull();
    ExpressionContext expr = retStmt.expression();
    assertThat(expr).isNotNull();
  }

  @Test
  public void test_foo3() throws IOException {
    String file = "pragma solidity 0.4.24;\n" +
      "\n" +
      "library SafeMath {\n" +
      "\n" +
      "  function add(uint256 _a, uint256 _b) internal pure returns (uint256) {\n" +
      "    uint256 c = _a + _b;\n" +
      "    require(c >= _a);\n" +
      "    return c;\n" +
      "  }\n" +
      "}\n" +
      "\n" +
      "contract Ownable {\n" +
      "  address public owner;\n" +
      "\n" +
      "\n" +
      "  event OwnershipRenounced(address indexed previousOwner);\n" +
      "  event OwnershipTransferred(\n" +
      "    address indexed previousOwner,\n" +
      "    address indexed newOwner\n" +
      "  );\n" +
      "\n" +
      "  constructor() public {\n" +
      "    owner = msg.sender;\n" +
      "  }\n" +
      "\n" +
      "  modifier onlyOwner() {\n" +
      "    require(msg.sender == owner);\n" +
      "    _;\n" +
      "  }\n" +
      "}\n" +
      "\n" +
      "\n" +
      "\n" +
      "contract Escrow is Ownable {\n" +
      "  using SafeMath for uint256;\n" +
      "\n" +
      "  event Deposited(address indexed payee, uint256 weiAmount);\n" +
      "  event Withdrawn(address indexed payee, uint256 weiAmount);\n" +
      "\n" +
      "  mapping(address => uint256) private deposits;\n" +
      "\n" +
      "  function depositsOf(address _payee) public view returns (uint256) {\n" +
      "    return deposits[_payee];\n" +
      "  }\n" +
      "\n" +
      "  function deposit(address _payee) public onlyOwner payable {\n" +
      "    uint256 amount = msg.value;\n" +
      "    deposits[_payee] = deposits[_payee].add(amount);\n" +
      "\n" +
      "    emit Deposited(_payee, amount);\n" +
      "  }\n" +
      "\n" +
      "  function withdraw(address _payee) public onlyOwner {\n" +
      "    uint256 payment = deposits[_payee];\n" +
      "    assert(address(this).balance >= payment);\n" +
      "\n" +
      "    deposits[_payee] = 0;\n" +
      "\n" +
      "    _payee.transfer(payment);\n" +
      "\n" +
      "    emit Withdrawn(_payee, payment);\n" +
      "  }\n" +
      "}\n" +
      "\n" +
      "\n" +
      "contract PullPayment {\n" +
      "  Escrow private escrow;\n" +
      "\n" +
      "  constructor() public {\n" +
      "    escrow = new Escrow();\n" +
      "  }\n" +
      "\n" +
      "  function withdrawPayments() public {\n" +
      "    address payee = msg.sender;\n" +
      "    escrow.withdraw(payee);\n" +
      "  }\n" +
      "  function payments(address _dest) public view returns (uint256) {\n" +
      "    return escrow.depositsOf(_dest);\n" +
      "  }\n" +
      "\n" +
      "  function asyncTransfer(address _dest, uint256 _amount) internal {\n" +
      "    escrow.deposit.value(_amount)(_dest);\n" +
      "  }\n" +
      "}";

    SolidityParsingPhase parser = new SolidityParsingPhase();
    SourceUnitContext suc = parser.parse(file);
    assertThat(suc).isNotNull();
  }
}
