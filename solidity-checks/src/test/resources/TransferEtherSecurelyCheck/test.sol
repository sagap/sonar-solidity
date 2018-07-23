pragma solidity ^0.4.24;

contract EtherReceiver {

    function () public payable {}
}

contract EtherSender {

    EtherReceiver private receiverAdr = new EtherReceiver();

    function sendEther(uint _amount) public payable {
        address recv = address(receiverAdr);
        if (!recv.send(_amount)) { // Noncompliant
        //   ^^^^^^^^^^^^^^^^^^
            //handle failed send
        }
    }

    function transferEther(uint _amount) public payable {
        address recv = address(receiverAdr);
        recv.transfer(_amount);
    }
}
