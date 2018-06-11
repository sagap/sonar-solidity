package org.sonarsource.solidity;

import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;
import org.sonarsource.solidity.SolidityParser.ContractDefinitionContext;
import org.sonarsource.solidity.SolidityParser.ContractPartContext;
import org.sonarsource.solidity.SolidityParser.IdentifierContext;
import org.sonarsource.solidity.SolidityParser.PragmaDirectiveContext;
import org.sonarsource.solidity.SolidityParser.SourceUnitContext;
import org.sonarsource.solidity.SolidityParser.StructDefinitionContext;
import org.sonarsource.solidity.SolidityParser.VariableDeclarationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityParserTest {

  @Test
  public void test_parsing() {
    String test = "pragma solidity ^0.4.22;\n" +
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
    //
    CharStream cs = CharStreams.fromString(test);
    SolidityLexer sl = new SolidityLexer(cs);
    TokenStream tokens = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokens);

    // ... source unit context is the root of the parse tree
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

}
