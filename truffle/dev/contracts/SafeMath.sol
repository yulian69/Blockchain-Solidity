pragma solidity ^0.4.19;


library SafeMath {
    function mul(uint256 a, uint256 b) internal pure returns (uint256) {
        if (a == 0) {
          return 0;
        }
        uint256 c = a * b;
        assert(c / a == b);
        return c;
    }

    function div(uint256 a, uint256 b) internal pure returns (uint256) {
        uint256 c = a / b;
        return c;
    }
    
    function sub(uint256 a, uint256 b) internal pure returns (uint256) {
        assert(b <= a);
        return a - b;
    }
    
    function add(uint256 a, uint256 b) internal pure returns (uint256) {
        uint256 c = a + b;
        assert(c >= a);
        return c;
    }
    
    function pow(uint256 a, uint256 b) internal pure returns (uint256) {
        if (a == 0 && b == 0) {
            revert();
        }
        if (a == 0 ) {
            return 0;
        }
        if (b == 0) {
            return 1;
        }
        uint256 c = a**b;
        assert(c >= a);
        return c;
    }
    
    function bytesToBytes(bytes b, uint offset, uint length) internal pure returns (bytes) {
        require(b.length >= add(offset,length));
        
        bytes memory out = new bytes(length);
        for (uint i = 0; i < length; i++) {
            out[i] = b[offset+i];
        }
        return out;
    }
    
    function bytesToBytes32(bytes b, uint offset) internal pure returns (bytes32) {
        bytes32 out;
        for (uint i = 0; i < 32; i++) {
            out |= bytes32(b[offset+i] & 0xFF) >> (i*8);
        }
        return out;
    }
}