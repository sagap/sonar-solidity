contract temp_contract{
    function kill()
    { 
	uint8 i = 0;
        while(i<20){
	  i++;
          if (i == 10 && i < 20)
          {
            break;  // kills this contract and sends remaining funds back to creator
          }
	}
    }
}
