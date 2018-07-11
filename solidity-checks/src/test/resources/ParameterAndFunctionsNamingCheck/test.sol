pragma solidity ^0.4.24;

contract test{
    function foo(uint8 a,uint8 foo) pure private{ // Noncompliant
        a = 1;
        foo=2;
    }
}