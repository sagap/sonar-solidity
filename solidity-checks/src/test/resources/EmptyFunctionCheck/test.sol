pragma solidity ^0.4.24;

contract test_compliant{   // Compliant
    address creator;
    uint8[10] integers;
    string constant a = "1";
    
    function foo() public returns (uint8)     // Compliant
    {
        bytes32 byteText = "Hello";
        integers[0] = 1;
        return 1;
    }
}

contract test_non
{
    uint8 x = 0;
    modifier testAPI {
        if(true) x = 1;
        x = 2;
        _;
    }
    
    function foo() public returns (uint8){     // Noncompliant {{Function should not be empty.}}
//^[sc=4;el=+2;ec=4]             
    }
}
