pragma solidity ^0.4.24;

contract test_cognitive_complexity8{
    address creator;
    uint8[10] integers;
    function foo() public 
    {
        creator = msg.sender;       
        uint8 x = 10;
        uint8 y = x > 1 && x > 5? 2 : 3;
          while(true){
            if(x > 2){
                    x = 1;
                }else if(x > 5){
                    if(x < integers.length && x > 10 && false)  
                    {
                      integers[x] = x;
                      if(x > 1 && x > 2){
                        x = 5;     
                    }else{
                          x= 5;
                      }
                    }else{
                        x = 10;
                    }
                }
            else if(x < 4){
                x = 1;
            }
            else if(x < 3){
                x = 1;
            }
            else{
                x = 1;
            }
        }
    }
}
