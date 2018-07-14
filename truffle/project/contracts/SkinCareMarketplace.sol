pragma solidity ^0.4.24;

import "./Ownable.sol";
import "./SafeMath.sol";
import "./LibSkinCareMarketplace.sol";
import "./ERC20Interface.sol";
import "./ApproveAndCallFallBack.sol";

contract SkinCareMarketplace is ApproveAndCallFallBack, Ownable {
    using LibSkinCareMarketplace for LibSkinCareMarketplace.MarketplaceData; 
    LibSkinCareMarketplace.MarketplaceData marketplaceData; 
    
    using SafeMath for uint;
    using SafeMath for bytes;
    
    event AddSeller(bytes32 indexed sellerId, string ipfs, uint timestamp);
    event UpdateSeller(bytes32 indexed sellerId, string ipfs, uint timestamp);
    
    event AddBuyer(bytes32 indexed buyerId, string ipfs, uint timestamp);
    event UpdateBuyer(bytes32 indexed buyerId, string ipfs, uint timestamp);
    
    event BuyItem(bytes32 indexed buyerId, bytes32 indexed sellerId, uint indexed day, bytes32 itemId, uint quantity, uint amount, uint timestamp);
    event AddItem(bytes32 indexed itemId, bytes32 indexed sellerId, string ipfs, uint price, uint quantity, uint timestamp);
    event UpdateItem(bytes32 indexed itemId, bytes32 indexed sellerId, string ipfs, uint price, uint quantity, uint timestamp);
   
    event TransferTokens(address addr, uint tokens, uint timestamp);
     
    modifier onlyNewSeller(address sender) {
        require(!marketplaceData.sellers[marketplaceData.sellerAddresses[sender]].exist);
        _;
    }
    
    modifier onlyExistingSeller() {
        require(marketplaceData.sellers[marketplaceData.sellerAddresses[msg.sender]].exist);
        _;
    }
    
    modifier onlyNewBuyer() {
        require(!marketplaceData.buyers[marketplaceData.buyerAddresses[msg.sender]].exist);
        _;
    }
    
    modifier onlyExistingBuyer(address sender) {
        require(marketplaceData.buyers[marketplaceData.buyerAddresses[sender]].exist);
        _;
    }
    
    modifier onlyExistingItem(bytes32 itemId) {
        require(marketplaceData.items[itemId].exist && marketplaceData.items[itemId].sellerId == marketplaceData.sellerAddresses[msg.sender]);
        _;
    }
    
    modifier onlyToken(address _tokenAddress) {
        require(tokenAddress == _tokenAddress);
        _;
    }
    
    address tokenAddress;
    uint registerPrice;
    
    function () public payable {
        revert();
    }
    
    constructor(address _tokenAddress) public {
        tokenAddress = _tokenAddress;
        registerPrice = 20000;
    }
   
    function addSeller(address sender, string ipfs) internal onlyNewSeller(sender) {
        transferRegisterTokens(sender);
        marketplaceData.addSeller(sender, ipfs);
        emit AddSeller(marketplaceData.sellerAddresses[sender], ipfs, now);
    }
    
    function updateSeller(string ipfs) public onlyExistingSeller {
        marketplaceData.updateSeller(ipfs);
        emit UpdateSeller(marketplaceData.sellerAddresses[msg.sender], ipfs, now);
    }
    
    function getSellerByAddress() public view returns(bytes32, string) {
        return marketplaceData.getSellerByAddress();
    }
    
    function getSellerById(bytes32 sellerId) public view returns(address, string) {
        return marketplaceData.getSellerById(sellerId);
    }
    
    function getSellers() public view returns(bytes32[]) {
        return marketplaceData.ar_sellers;
    }
    
    function addItem(string ipfs, uint price, uint quantity) public onlyExistingSeller {
        bytes32 itemId = marketplaceData.addItem(ipfs, price, quantity);
        emit AddItem(itemId, marketplaceData.getSellerId(), ipfs, price, quantity, now);
    }
    
    function updateItem(bytes32 itemId, string ipfs, uint price, uint quantity) public onlyExistingItem(itemId) {
        marketplaceData.updateItem(itemId, ipfs, price, quantity);
        emit UpdateItem(itemId, marketplaceData.getSellerId(), ipfs, price, quantity, now);
    }
    
    function getItem(bytes32 itemId) public view returns(string,uint,uint) {
        return marketplaceData.getItem(itemId);
    }
    
    function getItems(bytes32 sellerId) public view returns(bytes32[]) {
        return marketplaceData.sellers[sellerId].items;
    }
   
    function getBuyerByAddress() public view returns(bytes32, string) {
        return marketplaceData.getBuyerByAddress();
    }
    
    function getBuyer(bytes32 buyerId) public view returns(address, string) {
        return marketplaceData.getBuyerById(buyerId);
    }
    
    function updateBuyer(string ipfs) public onlyExistingBuyer(msg.sender) {
        marketplaceData.updateBuyer(ipfs);
        emit UpdateBuyer(marketplaceData.getBuyerId(), ipfs, now);
    }
    
    function registerAndBuy(address sender, string ipfs, bytes32 itemId, uint tokens) onlyNewBuyer internal {
        bytes32 buyerId = marketplaceData.registerBuyer(sender, ipfs);
        emit AddBuyer(buyerId, ipfs, now);
        buy(sender, itemId, tokens);
    }
    
    function buy(address sender, bytes32 itemId, uint tokens) internal onlyExistingBuyer(sender) {
        transferBuyTokens(sender, marketplaceData.getSellerAddressByItemId(itemId), tokens);
        
        uint quantity = marketplaceData.buy(itemId, tokens);
        bytes32 sellerId = marketplaceData.getSellerIdByItemId(itemId);
       
        emit BuyItem(marketplaceData.buyerAddresses[sender], sellerId, now/3600, itemId, quantity, tokens, now);
    }
    
    function receiveApproval(address sender, uint tokens, address _tokenAddrress, bytes data) public onlyToken(_tokenAddrress) {
        require(data.length >= 1);
        
        if ( data[data.length-1] == byte(1) ) {
            require(tokens >= registerPrice);
            addSeller(sender, string(data.bytesToBytes(0,data.length-1)));
        } else if ( data[data.length-1] == byte(2) ) {
            require(data.length >= 34);
            registerAndBuy(sender, string(data.bytesToBytes(32,data.length-33)), data.bytesToBytes32(0), tokens);
        } else if ( data[data.length-1] == byte(3) ) {
            require(data.length >= 33);
            buy(sender, data.bytesToBytes32(0), tokens);
        } else {
            revert();
        }
    }
    
    function setTokenAddress(address _tokenAddress) public onlyOwner {
        tokenAddress = _tokenAddress;
    }
    
    function getTokenAddress() public view returns(address) {
        return tokenAddress;
    }
    
    function transferTokens(address addr, uint tokens) public onlyOwner {
        ERC20Interface(tokenAddress).transfer(addr, tokens);
        emit TransferTokens(addr, tokens, now);
    }
    
    function transferRegisterTokens(address sender) internal {
        ERC20Interface(tokenAddress).transferFrom(sender, address(this), registerPrice);
    }
    
    function transferBuyTokens(address sender, address seller, uint tokens) internal {
        uint sellerTokens = tokens.mul(95).div(100);
        
        ERC20Interface(tokenAddress).transferFrom(sender, address(this), tokens);
        ERC20Interface(tokenAddress).transfer(seller, sellerTokens);
    }
    
    function transferAllTokens() public onlyOwner {
        transferTokens(owner, ERC20Interface(tokenAddress).balanceOf(address(this)));
    }
    
    function kill() public onlyOwner {
        transferAllTokens();
        selfdestruct(owner);
    }
}

