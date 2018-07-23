pragma solidity 0.4.24;

import "github.com/oraclize/ethereum-api/oraclizeAPI.sol";

contract test is usingOraclize {
    string public EURUSD;
    function updatePrice() public payable {
        if (oraclize_getPrice("URL") > this.balance) {
            //Handle out of funds error
        } else {
            oraclize_query("URL", "json(http://)");
        }
    }
    
    function __callback(bytes32 myid, address result) public {    // Compliant
        require(msg.sender != oraclize_cbAddress());
    }
    
    function __callback(string result) public {  // Noncompliant {{Callback function should use require to check if the results from the external call are valid.}}
        //   ^^^^^^^^^^   
    }
    
    function () payable public{
    }
    
    function __callback(bytes32 myid, string result) public {   // Compliant
        __callback(myid, result, new bytes(0));
    }
    
    function __callback(bytes32 myid, string result, bytes proof) public {    // Noncompliant
      return;
      myid; result; proof; // Silence compiler warnings
    }
    function __callback(bytes32 myid, uint resulttt) {    // Noncompliant
    }
}

contract test2{
}
