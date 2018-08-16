pragma solidity 0.4.24;

contract test {

    mapping(address => uint) b;

    function deposit() public payable {
        b[msg.sender] = msg.value;
    }

    function withdraw(uint amount) public {
        require(b[msg.sender] >= amount);
        b[msg.sender] -= amount;
        msg.sender.transfer(amount);      // Compliant
    }
    
    function withdraw2(uint amount) public {
        require(b[msg.sender] >= amount);
        msg.sender.transfer(amount);     // Noncompliant {{External call should always be last statement in a function.}}
     // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        b[msg.sender] -= amount;
    }

    function withdraw3(uint amount) public {
        require(b[msg.sender] >= amount);
        msg.sender.send(amount);     // Noncompliant
     // ^^^^^^^^^^^^^^^^^^^^^^^^
        b[msg.sender] -= amount;
    }
    
    function withdraw4(uint amount) public {
        require(b[msg.sender] >= amount);
        b[msg.sender] -= amount;
        msg.sender.send(amount);
    }
    
    function withdraw5(uint amount5) public {
        require(b[msg.sender] >= amount5);
        b[msg.sender] -= amount5;
        if(!msg.sender.send(amount5)){    // Compliant
          uint a = 1;                       
        }
        return;
    }
    
    function withdraw6(uint amount6) public {
        require(b[msg.sender] >= amount6);
        if(true){
          msg.sender.send(amount6);   // Comp;iant, 1st version of this pattern does not take into account nested cases
        }
        b[msg.sender] -= amount6;
    }    
}
