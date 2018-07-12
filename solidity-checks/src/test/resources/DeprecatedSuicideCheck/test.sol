pragma solidity ^0.4.12;

contract test{
    
    address x = 0x123;
    address myAddress = this;
    
    function foo() public{
        suicide(x);       // Noncompliant {{"Selfdestruct" should be used instead of the deprecated "suicide".}}
     // ^^^^^^^^^^
        selfdestruct(x);
    }
}
