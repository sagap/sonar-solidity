pragma solidity ^0.4.16;

contract C {
    function f(uint x ) public pure returns (uint8) {
        uint8 y = x > 1 ? 2 : 3;
        return x > 5 && y >10  ? 2 : 1;
    }
}