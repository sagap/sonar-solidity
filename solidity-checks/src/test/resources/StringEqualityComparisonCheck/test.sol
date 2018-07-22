pragma solidity 0.4.24;

import "github.com/ethereum/dapp-bin/library/stringUtils.sol";

contract test {
    function foo() returns (bool){
        string memory a = "hello";
        string memory b = "world";
        if(StringUtils.equal(a,b)){		// Noncompliant
        }

        return StringUtils.equal(a, b); 	// Noncompliant
    }
}
