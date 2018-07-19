pragma solidity 0.4.18;

contract MyContract {

    address owner;

    function MyContract() public {
        owner = msg.sender;
    }

    function sendTo(address receiver, uint amount) public {
        require(tx.origin == owner);  // Noncompliant {{Avoid usage of "tx,origin", in authorization cases it is a bottleneck and it will not be compatible in the future.}}
            //  ^^^^^^^^^
        address a = tx.origin;     // Noncompliant
        
        if(tx.origin != 0x0){    // Noncompliant
        }
        Assert.equal(meta.getBalance(tx.origin), expected, "");  // Noncompliant
    }
}