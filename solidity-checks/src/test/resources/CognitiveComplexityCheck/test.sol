pragma solidity ^0.4.24;

contract test_cognitive_complexity6{
    address creator;
    uint8[10] integers;
    function foo() public { // Noncompliant
//^[sc=4;el=6;ec=26]    
        creator = msg.sender;       
        uint8 x = 0;
        while(true){
            if(x > 0){
                if(x > 2){
                    x = 1;
                }
                if(x > 5){
                    while(x < integers.length)  
                    {
                      integers[x] = x;
                      if(x%2 == 0){
                            x=1;
                      }
                      if(x%2 == 1 && x != 5){
                          x= 5;
                      }
                    }
                }
            }
        }
    }
    
    function() external{
    }
    
    function f(uint8 a) pure internal;
}
