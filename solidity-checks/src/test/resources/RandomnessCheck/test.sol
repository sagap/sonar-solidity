pragma solidity 0.4.24;

contract foo{

  function randomNumber() internal view returns (uint) {
    return uint(blockhash(block.number - 1));       // FN 
  }
  
  function randomNumber2() internal view {
    var a = uint(blockhash(block.number - 1));   
  }
  Race race;
  function random(uint Max) constant private returns (uint256 result){
    race = (new Race).value(raceKickstarter)(); 
    uint256 salt = 1;
    uint8 b1;
    salt = block.number;   // FN
    uint256 x = salt * 100/Max;
    uint256 y = salt * block.number/(salt % 5) ;    // Noncompliant {{You should not use block to create a random number, you should use "keccak256()".}}
            //         ^^^^^^^^^^^^
    uint256 seed = block.number/3 + (salt % 300); // Noncompliant
                 //^^^^^^^^^^^^
    uint256 h = uint256(block.blockhash(seed));    // Noncompliant
    uint random1 = uint(keccak256(x, block.blockhash(block.number))); // Compliant
    
    uint random2 = uint(block.blockhash(block.number/3 + (salt % 300)));  // Noncompliant
    uint random3 = uint(block.difficulty(1));     // this line is for coverage
    uint random4 = uint(blockhash(1));  // Noncompliant
    return uint256((h / x)) % Max + 1; //random number between 1 and Max
  }
}
