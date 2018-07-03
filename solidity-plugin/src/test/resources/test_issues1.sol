pragma solidity ^0.4.24;

contract foo1{
    address creator;
    uint8[10] integers;
    function foo() public returns (uint8) 
    {
        integers[0] = 1;
        return 1;
    }
}