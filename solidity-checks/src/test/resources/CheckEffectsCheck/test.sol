pragma solidity 0.4.24;

contract test {

    mapping(address => uint) b;

    function deposit() public payable { // Noncompliant {{Checks should be done at the beggining of any function making external calls.}}
           //^^^^^^^
        b[msg.sender] = msg.value;
    }

    function withdraw(uint amount) public {
        require(b[msg.sender] >= amount);

        b[msg.sender] -= amount;

        msg.sender.transfer(amount);
    }
}