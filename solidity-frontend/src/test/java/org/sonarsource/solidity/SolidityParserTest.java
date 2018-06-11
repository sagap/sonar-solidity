package org.sonarsource.solidity;

import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;

public class SolidityParserTest {

  @Test
  public void test() {
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
    CharStream cs = CharStreams.fromString(test);
    SolidityLexer sl = new SolidityLexer(cs);
    List<Token> ts = (List<Token>) sl.getAllTokens();
    TokenStream tokens = new CommonTokenStream(sl);
    SolidityParser parser = new SolidityParser(tokens);

  }

}
