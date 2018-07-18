package org.sonarsource.solidity.frontend;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;

public class SolidityTokensInfo {

  private SolidityTokensInfo() {
  }

  protected static final String GRAMMAR_FILENAME = "Solidity.g4";

  protected static final String[] ruleNames = {
    "sourceUnit", "pragmaDirective", "pragmaName", "pragmaValue", "version",
    "versionOperator", "versionConstraint", "importDeclaration", "importDirective",
    "contractDefinition", "inheritanceSpecifier", "contractPart", "stateVariableDeclaration",
    "usingForDeclaration", "structDefinition", "constructorDefinition", "modifierDefinition",
    "modifierInvocation", "functionDefinition", "returnParameters", "modifierList",
    "eventDefinition", "enumValue", "enumDefinition", "parameterList", "parameter",
    "eventParameterList", "eventParameter", "functionTypeParameterList", "functionTypeParameter",
    "variableDeclaration", "typeName", "userDefinedTypeName", "mapping", "functionTypeName",
    "storageLocation", "stateMutability", "block", "statement", "expressionStatement",
    "ifStatement", "whileStatement", "simpleStatement", "forStatement", "inlineAssemblyStatement",
    "doWhileStatement", "continueStatement", "breakStatement", "returnStatement",
    "throwStatement", "emitStatement", "variableDeclarationStatement", "variableDeclarationList",
    "identifierList", "elementaryTypeName", "expression", "primaryExpression",
    "expressionList", "nameValueList", "nameValue", "functionCallArguments",
    "functionCall", "assemblyBlock", "assemblyItem", "assemblyExpression",
    "assemblyCall", "assemblyLocalDefinition", "assemblyAssignment", "assemblyIdentifierOrList",
    "assemblyIdentifierList", "assemblyStackAssignment", "labelDefinition",
    "assemblySwitch", "assemblyCase", "assemblyFunctionDefinition", "assemblyFunctionReturns",
    "assemblyFor", "assemblyIf", "assemblyLiteral", "subAssembly", "tupleExpression",
    "elementaryTypeNameExpression", "numberLiteral", "identifier"
  };

  private static final String[] _LITERAL_NAMES = {
    null, "'pragma'", "';'", "'^'", "'~'", "'>='", "'>'", "'<'", "'<='", "'='",
    "'as'", "'import'", "'*'", "'from'", "'{'", "','", "'}'", "'contract'",
    "'interface'", "'library'", "'is'", "'('", "')'", "'using'", "'for'",
    "'struct'", "'constructor'", "'modifier'", "'function'", "'returns'",
    "'event'", "'enum'", "'['", "']'", "'.'", "'mapping'", "'=>'", "'memory'",
    "'storage'", "'calldata'", "'if'", "'else'", "'while'", "'assembly'",
    "'do'", "'return'", "'throw'", "'emit'", "'var'", "'address'", "'bool'",
    "'string'", "'byte'", "'++'", "'--'", "'new'", "'+'", "'-'", "'after'",
    "'delete'", "'!'", "'**'", "'/'", "'%'", "'<<'", "'>>'", "'&'", "'|'",
    "'=='", "'!='", "'&&'", "'||'", "'?'", "':'", "'|='", "'^='", "'&='",
    "'<<='", "'>>='", "'+='", "'-='", "'*='", "'/='", "'%='", "'let'", "':='",
    "'=:'", "'switch'", "'case'", "'default'", "'->'", null, null, null, null,
    null, null, null, null, null, null, null, null, "'anonymous'", "'break'",
    "'constant'", "'continue'", "'external'", "'indexed'", "'internal'", "'payable'",
    "'private'", "'public'", "'pure'", "'view'"
  };
  private static final String[] _SYMBOLIC_NAMES = {
    null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, "Int", "Uint", "Byte", "Fixed",
    "Ufixed", "VersionLiteral", "BooleanLiteral", "DecimalNumber", "HexNumber",
    "NumberUnit", "HexLiteral", "ReservedKeyword", "AnonymousKeyword", "BreakKeyword",
    "ConstantKeyword", "ContinueKeyword", "ExternalKeyword", "IndexedKeyword",
    "InternalKeyword", "PayableKeyword", "PrivateKeyword", "PublicKeyword",
    "PureKeyword", "ViewKeyword", "Identifier", "StringLiteral", "WS", "COMMENT",
    "LINE_COMMENT"
  };
  public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

  protected static final String[] tokenNames;

  static {
    tokenNames = new String[_SYMBOLIC_NAMES.length];
    for (int i = 0; i < tokenNames.length; i++) {
      tokenNames[i] = VOCABULARY.getLiteralName(i);
      if (tokenNames[i] == null) {
        tokenNames[i] = VOCABULARY.getSymbolicName(i);
      }
      if (tokenNames[i] == null) {
        tokenNames[i] = "<INVALID>";
      }
    }
  }

  public static String[] getTokenNames() {
    return tokenNames;
  }

  public static Vocabulary getVocabulary() {
    return VOCABULARY;
  }

  public static String getGrammarFileName() {
    return GRAMMAR_FILENAME;
  }

  public static String[] getRuleNames() {
    return ruleNames;
  }
}
