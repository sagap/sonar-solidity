pragma solidity 0.4.24;

contract foo {
    bytes b = "hello";
    bytes32 b1 = sha3(b);   // Noncompliant {{"sha3" is deprecated, use "keccak256" instead.}}
              // ^^^^
    bytes32 b2 = keccak256(b);   // Compliant
    function f(){
      if(sha3(b).length == 0){ // Noncompliant
      }
    }
}