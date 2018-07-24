pragma solidity 0.4.24;

contract test{
    event LogTest(bytes b);
    
    byte[] a;   // Noncompliant
    uint[] myArray;
    constructor(bytes b) public{
        byte[] memory a1;   // Noncompliant   {{"bytes" should be used instead of "byte[]", due to lower gas consumption.}}
    //  ^^^^^^
        emit LogTest(b);
    }
    
    function foo(){
        byte[] memory a = new byte[](7);   // Noncompliant
    }
}
