pragma solidity ^0.4.24;

contract test{
    function foo(uint8 a,uint8 foo) pure private{ // Noncompliant
        a = 1;
        foo=2;
    }

    function foo2(){}
    function foo2(uint8, string foo2) public; // Noncompliant
    function ()internal {}
}
