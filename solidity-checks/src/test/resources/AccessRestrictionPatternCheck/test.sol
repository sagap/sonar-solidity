pragma solidity 0.4.24;

contract foo {
   uint8 numCampaigns = 0; 
   function foof4() external payable onlyOwner1;
   function foof5() payable onlyOwner1;
   function foof6() external payable;
   function foof7(uint amount) external payable onlyOwner1;
   modifier onlyOwner1 {
    require(msg.sender == tx.origin,"Only owner can call this function.");
    require(numCampaigns > 3);
    _;
  }
  

  function newCampaign(address beneficiary, uint goal) public returns (uint campaignID){
  }
  function contribute(uint campaignID) public payable onlyOwner(campaignID){
 }
 
  function foof1(uint campaignID) public payable{  // Noncompliant {{You should restrict access using modifiers, apply Access Restriction Pattern.}}
//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  }
   
  function foof2() public payable {  // Compliant
  }
 
  function foof3() public payable onlyOwner1{  // Compliant
  }
  
  function foof8(uint amount) external payable{}  // Noncompliant

  modifier onlyOwner(uint c) {
    require(msg.sender == tx.origin,"Only owner can call this function.");
    require(c > 3);
    _;
  }
}