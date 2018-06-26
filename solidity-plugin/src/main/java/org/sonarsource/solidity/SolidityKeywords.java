package org.sonarsource.solidity;

public class SolidityKeywords {

  private SolidityKeywords() {
  }

  private static final String[] KEYWORDS = {
    "abstract",
    "catch",
    "final",
    "in",
    "let",
    "inline",
    "match",
    "type",
    "of",
    "null",
    "static",
    "relocatable",
    "try",
    "typeof",
    "protected",
    "inheritable",
    "pragma",
    "as",
    "import",
    "from",
    "contract",
    "interface",
    "library",
    "is",
    "using",
    "for",
    "struct",
    "constructor",
    "modifier",
    "function",
    "returns",
    "event",
    "enum",
    "mapping",
    "memory",
    "storage",
    "if",
    "else",
    "while",
    "assembly",
    "do",
    "return",
    "throw",
    "emit",
    "var",
    "new",
    "after",
    "delete",
    "switch",
    "case",
    "default",
    "anonymous",
    "break",
    "constant",
    "continue",
    "external",
    "indexed",
    "internal",
    "payable",
    "private",
    "public",
    "pure",
    "view"
  };

  private static final String[] KEYWORD_TYPES = {
    "bool",
    "int",
    "uint",
    "uint8",
    "uint16",
    "uint24",
    "uint32",
    "uint128",
    "uint256",
    "int8",
    "int256",
    "fixed",
    "ufixed",
    "ufixedMxN",
    "fixedMxN",
    "ufixed128x18",
    "fixed128x18",
    "address",
    "byte",
    "bytes1",
    "bytes32",
    "string",
    "hex",
    "bytes"
  };

  public static String[] get() {
    return KEYWORDS;
  }

  public static String[] getKeyowrdTypes() {
    return KEYWORD_TYPES;
  }
}
