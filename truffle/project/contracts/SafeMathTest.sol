pragma solidity ^0.4.24;

import "./SafeMath.sol";

contract SafeMathTest {
    using SafeMath for uint;
    
    function testSafeMath() public pure {
        uint a = 0;
        
        require(a.mul(10)==0);
        require(a.pow(10)==0);
        
        a = 10;
        require(a.pow(0)==1);
    }
}