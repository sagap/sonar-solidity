contract temp_contract{
    
    event Aborted();
    function kill()
    { 
  uint8 i = 0;
        while(i<20){
    i++;
          if (i == 10)
          {
            break;  // kills this contract and sends remaining funds back to creator
          }
          else if(i ==11){
              for(int j =0;j<2;j++){
                  throw ;
              }
              do{
                  i++;
              }while(i<10);
              emit Aborted();
              continue;
          }
  }
    }
}