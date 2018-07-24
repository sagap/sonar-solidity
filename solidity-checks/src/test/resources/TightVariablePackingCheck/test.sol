pragma solidity 0.4.24;

contract test{
    
    bytes32 alpha;
    bytes beta;// Noncompliant {{Use a statistically sized variable, instead of 'bytes'}}
    int gamma;// Noncompliant {{Use a statistically sized variable, instead of 'int'}}
    uint delta;// Noncompliant {{Use a statistically sized variable, instead of 'uint'}}
    mapping(address => uint8) add;
  
  struct TestStruct{
    uint8 a;
    bytes32 b;
    int8 c;
    bool d;
    int n1;   // Noncompliant {{Use a statistically sized variable, instead of 'int'}}
//  ^^^
    uint n2;   // Noncompliant {{Use a statistically sized variable, instead of 'uint'}}
    bytes n3;   // Noncompliant {{Use a statistically sized variable, instead of 'bytes'}}
//  ^^^^^
  }
  
  function foo(int a, int8 c){
    int epsilon = 1;  // Noncompliant
    uint theta = 2;  // Noncompliant
    bytes memory itta = "1";  // Noncompliant
    
    (uint a1, uint a2, uint a3) = foo2(); // Noncompliant 3
    uint256 d;
    ( , ,d ) = foo2();
  }
  
  function foo2() public returns (uint,uint,uint){
    return (1,2,3);
  }
}
