pragma solidity 0.4.24;

contract foo{

  bytes32 a = 0x0;
  
  function f1(uint t){ // Noncompliant {{You should check with require the validity of all the parameters.}}
        // ^^
  }
  
  function f2(uint t) external payable mod1(t){    // Compliant
      
  }
  
  function f3(uint t, address _a) public payable mod1(t){    // Noncompliant
        // ^^
    uint a = 1;
    while(true)
      break;
  }

  function f4(uint t, address _a, bytes) external payable mod1(t){    // Compliant
    uint a2 = 10;
    require(_a == 0);
  }

  function f5(uint t, bytes _b) external payable mod1(t){    // Compliant
    require(t == 0);
    a = 0x1;
    assert(t == 0);
    require(a == keccak256(_b));    // Compliant
  }
  
  function f6(uint t) private{
  }
   
  function f7() external{
  }

  function f8(uint t, address _a) public pure mod1(t){    // Compliant
  }
  
  modifier mod1(uint _t) {
      require(_t > 1);
      _;
  }
}