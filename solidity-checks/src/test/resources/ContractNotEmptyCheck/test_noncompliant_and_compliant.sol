pragma solidity ^0.4.24;

contract test_compliant{   // Compliant
    address creator;
    uint8[10] integers;
    function foo() public returns (uint8) 
    {
        integers[0] = 1;
        return 1;
    }
}

contract test_non{  // Noncompliant {{Contract should not be empty}}

}