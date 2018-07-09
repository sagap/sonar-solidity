pragma solidity ^0.4.24;

contract test_non_compliant{   // Compliant
    function foo() public returns (uint8) 
    {         // Noncompliant {{Move this open curly brace to the end of the previous line.}}     
 // ^
      if(true){  
      }
      if(false)
      {} // Noncompliant {{Move this open curly brace to the end of the previous line.}}
  //  ^  
      if(true && false
      ){                //  Noncompliant
      } 
      if(true 
      && false){ // Compliant 
      }  
      else if(true){
      }
      else if(false)
      {     // Noncompliant
      
      }
      else
      { // Noncompliant
      }
      return 8;
    
      for(uint8 i=0;i<10;i++){
      }
      for(uint8 i2=0;i2<10;i2++)
      {     // Noncompliant
      }
      for(uint8 i3=0;i3<10
      ;i3++){        // Compliant
      }
      for(uint8 i4=0;i4<10
      ;i4++)i4++;
        
      while(true){
      }
      while(false)
      {         // Noncompliant
      }
      do{
      }while(true);
      do
      {       // Noncompliant
      }while(true);
      bool temp;
      while(true)
        temp = false;
    }
   function foo2() public returns (uint8);
    function foo3() public returns (uint8, 
    string)
    {   // Noncompliant
      if(true   && 
      false){  // Compliant
      }
      else{  // Compliant
      }
    }
  
  struct s{
   uint a; uint b; }   // Compliant
      
  struct s1
  { uint a; uint b; }   // Noncompliant
  
   address public seller;
    modifier onlySeller() 
    {      // Noncompliant
        require(msg.sender == seller);
        _;
    }
    function owned() public { owner = msg.sender; }
    address owner;
    modifier onlyOwner {
        require(msg.sender == owner);
        _;
    }
    function abort() public onlySeller { // Modifier usage
        bool a;
        if(true)
          a = true;
        else
          a = false;
            do
        a = false;
        while(true);
    }
    enum State { Created, Locked, Inactive } // Compliant
    enum State1 
    { Created, Locked, Inactive } // Noncompliant
    
    function foo4()
    public {     // Compliant
    }
    
    function foo5()
    {     // Noncompliant
    }
}
contract test_non_compliant2
{   // Noncompliant
}

