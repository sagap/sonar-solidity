contract test_cognitive_complexity1{
    address creator;
    uint8[10] integers;
    function foo() 
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
              else if(x%2 == 1){
                  continue;
              }
              else{
                  x = 5;
              }
              uint8 cnt = 0;
              uint8 i = 0;
                for(i;i<2;i++) {
                    cnt++;
                }
            }
        }
        if(integers.length == 10){
            x++;
        }
    }
}