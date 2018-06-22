pragma solidity ^0.4.24;

contract test_cognitive_complexity4{
    address creator;
    uint8[10] integers;
    function foo() public
    {
        creator = msg.sender;       
        uint8 x = 0;
        if(x > 0){
            while(x < integers.length)  
            {
              integers[x] = x;
              if(x%2 == 0){
                    break;
              }
              else if(x%2 == 1 && x != 5){
                  continue;
              }
              else{
                  x = 5;
                  break;
              }
              uint8 cnt = 0;
              uint8 i = 0;
                for(i;i<2;i++) {
                    cnt++;
                }
            }
        }
        if(integers.length == 10 || integers.length == 12){
            x++;
        }
    }
}