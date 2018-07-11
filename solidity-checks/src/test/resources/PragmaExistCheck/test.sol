pragma solidity ^0.4.12;

contract test{
    function foo(uint8 a,uint8 foo) pure private{ // Noncompliant
        a = 1;
        foo=2;
    }
     
    function foo2()view public{
        uint8 a = 1;
    }
    function foo2(uint8, string foo2) public;
    function ()internal {}
}




contract t{}