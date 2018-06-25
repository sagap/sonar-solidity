pragma solidity ^0.4.16;

contract C {
    function f(uint x ) public pure returns (uint) {
        return x > 5 ? 2 : 1;
    }
}