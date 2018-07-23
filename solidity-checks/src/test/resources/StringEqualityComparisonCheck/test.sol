pragma solidity 0.4.24;

import "github.com/ethereum/dapp-bin/library/iterable_mapping.sol" as it_mapping;
import "github.com/ethereum/dapp-bin/library/math.sol";
import "github.com/ethereum/dapp-bin/library/features.sol";

contract t{
  function foo(uint x, uint k, uint m) payable public{
     Math.modExp(x,k,m);
  }

}
import "github.com/ethereum/dapp-bin/library/stringUtils.sol";

contract test {
    function foo() returns (bool){
        string memory a = "hello";
        string memory b = "world";
        if(StringUtils.equal(a,b)){   // Noncompliant
        }
        Math.modExp(1,2,3);
        StringUtils.indexOf("a", "aa");
        mortal.kill();
        return StringUtils.equal(a, b);   // Noncompliant
    }
}
