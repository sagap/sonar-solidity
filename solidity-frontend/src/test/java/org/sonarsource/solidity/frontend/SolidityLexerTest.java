package org.sonarsource.solidity.frontend;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SolidityLexerTest {

  private static SolidityLexer lexer;

  @Test
  public void basic_test() {
    CharStream cs = CharStreams.fromString("function");
    lexer = new SolidityLexer(cs);
    assertThat(lexer.getGrammarFileName()).isEqualTo("Solidity.g4");
    assertThat(lexer.getRuleNames()).hasSize(83);
    assertThat(lexer.getChannelNames()).hasSize(2);
    assertThat(lexer.getModeNames()).hasSize(1);
    assertThat(lexer.getSerializedATN()).startsWith("\3");
    assertThat(lexer.getATN()).isNotNull();
  }

  @Test
  public void tokenize_function() {
    CharStream cs = CharStreams.fromString("function");
    lexer = new SolidityLexer(cs);
    assertThat(lexer.getAllTokens().get(0).getType()).isEqualTo(28);
  }

  @Test
  public void tokenize_contract() {
    CharStream cs = CharStreams.fromString("contract");
    lexer = new SolidityLexer(cs);
    assertThat(lexer.getAllTokens().get(0).getType()).isEqualTo(17);
  }

  @Test
  public void tokenize_pragma() {
    CharStream cs = CharStreams.fromString("pragma");
    lexer = new SolidityLexer(cs);
    assertThat(lexer.getAllTokens().get(0).getType()).isEqualTo(1);
  }

  @Test
  public void tokenize_numeric_literal() {
    CharStream cs = CharStreams.fromString("1");
    lexer = new SolidityLexer(cs);
    assertThat(lexer.getAllTokens().get(0).getType()).isEqualTo(97);
  }

  @Test
  public void tokenize_boolean_literal() {
    CharStream cs = CharStreams.fromString("true");
    lexer = new SolidityLexer(cs);
    assertThat(lexer.getAllTokens().get(0).getType()).isEqualTo(96);
  }

  @Test
  public void tokenize_identifier() {
    String identifier = "identifier";
    CharStream cs = CharStreams.fromString(identifier);
    lexer = new SolidityLexer(cs);
    assertThat(lexer.getAllTokens().get(0).getType()).isEqualTo(114);
  }
}
