contract test_cognitive_complexity5{
    function foo() 
    {
        uint8 x = 0;
        uint8 i = 0;
        for(i;i<2;i++) {
          if(x%2 == 0){
                break;
          }
          else if(x%2 == 1){
              continue;
          }
          else{
              x = 5;
          }
        }
        if(x == 0){
          x = 2;
        }
        else{
          x = 10;
          for(uint8 j =0;j<10;j++){
              if(x==1){
                x++;
              }
          }
        }
    }
}