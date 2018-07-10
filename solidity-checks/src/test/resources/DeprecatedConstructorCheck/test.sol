pragma solidity ^0.4.24;

contract foo{
    constructor() {  // Compliant
    }
}

contract foo2{
    constructor(uint8) internal{  // Compliant
    }
}

contract foo3{
    function foo3(uint8){    // Noncompliant
    }
}

contract foo4{
    function foo4(uint8) public{    // Noncompliant
 // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    }
}

contract foo5{
    function foo5(){    // Noncompliant
 // ^^^^^^^^^^^^^^^^
    }
    
    function test(){  // Compliant
    }
}