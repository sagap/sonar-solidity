pragma solidity 0.4.24;

contract test{
    
  function foo(int a, int8 c){
    var epsilon = 1;  // Noncompliant {{"var" keyword is deprecated.}}
    var itta = "1";  // Noncompliant
 // ^^^^^^^^^^^^^^^
    var (a1, a2, a3) = foo2(); // Noncompliant
    var ( , ,d ) = foo2(); // Noncompliant
//  ^^^^^^^^^^^^^^^^^^^^^^
  }
}
