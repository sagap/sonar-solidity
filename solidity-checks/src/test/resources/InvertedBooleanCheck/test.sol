pragma solidity ^0.4.24;

contract test_compliant{   // Compliant
    address creator;
    uint8[10] integers;
    string constant a = "1";
    
    function foo() public returns (uint8) 
    {
        bool a1 = !true;   // Noncompliant
        integers[0] = 1;
        if(!(integers[0] == 1))    // Noncompliant {{Boolean expression should not be inverted.}}
        {   //^[sc=11;el=12;ec=29]
            integers[1] = 10;
        }
        
        return 1;
    }
        
     function noncompliant(uint256 a, uint8 i) public{
        if ( !(a == 2)) { }  // Noncompliant
        bool b = !(i < 10);   // Noncompliant
        b = !(i > 10);    // Noncompliant
        b = !(i != 10); // Noncompliant
        b = !(i <= 10); // Noncompliant
        b = !(i >= 10);// Noncompliant
    }

    function compliant(uint256 a, uint8 i) private{
      if (a != 2) { }
      bool b = (i >= 10);
    }
}

contract test_non
{
    uint8 x = 0;
    bool constant a2 = !(1==2);       // Noncompliant
                    // ^^^^^^^
    function foo2() public{
        if(x != 1 && x == 3){
            x = 1;
        }
    }
    
    modifier testAPI {
        if(true) x = 1;
        x = 2;
        _;
    }
}