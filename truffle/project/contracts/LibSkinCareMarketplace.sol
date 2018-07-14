pragma solidity ^0.4.24;

import "./SafeMath.sol";

library LibSkinCareMarketplace {
    using SafeMath for uint;
    
    struct Item {
        string ipfs;
        uint price;
        uint quantity;
        bytes32 sellerId;
        bool exist;
    }
    
    struct Seller {
        address addr;
        string ipfs;
        bytes32[] items;
        bool exist;
    }
    
    struct Buyer {
        address addr;
        string ipfs;
        bool exist;
    }
    
    struct MarketplaceData { 
        mapping(bytes32 => Item) items;
        mapping(bytes32 => Seller) sellers;
        mapping(address => bytes32) sellerAddresses;
        mapping(bytes32 => Buyer) buyers;
        mapping(address => bytes32) buyerAddresses;
         
        bytes32[] ar_sellers;
    }
    
    function getSellerId(MarketplaceData storage marketplaceData) internal view returns(bytes32) {
        return marketplaceData.sellerAddresses[msg.sender];
    }
    
    function getSellerByAddress(MarketplaceData storage marketplaceData) internal view returns(bytes32, string) {
        bytes32 sellerId = getSellerId(marketplaceData);
        Seller storage seller = marketplaceData.sellers[sellerId];
        require(seller.exist);
        return (sellerId, seller.ipfs);
    }
    
    function getSellerById(MarketplaceData storage marketplaceData, bytes32 sellerId) internal view returns(address, string) {
        Seller storage seller = marketplaceData.sellers[sellerId];
        require(seller.exist);
        return (seller.addr, seller.ipfs);
    }
    
    function getSellerIdByItemId(MarketplaceData storage marketplaceData, bytes32 itemId) internal view returns(bytes32) {
        Item storage item = marketplaceData.items[itemId];
        require(item.exist);
        return item.sellerId;
    }
    
    function getSellerAddressByItemId(MarketplaceData storage marketplaceData, bytes32 itemId) internal view returns(address) {
        Item storage item = marketplaceData.items[itemId];
        require(item.exist);
        Seller storage seller = marketplaceData.sellers[item.sellerId];
        require(seller.exist);
        return seller.addr;
    }
    
    function addSeller(MarketplaceData storage marketplaceData, address sender, string ipfs) internal {
        bytes32 sellerId = keccak256(abi.encodePacked(msg.sender, ipfs, blockhash(block.number-1), now));
        
        require(!marketplaceData.sellers[sellerId].exist);

        marketplaceData.sellers[sellerId] = Seller({addr : sender, ipfs : ipfs, items : new bytes32[](0), exist : true});
        marketplaceData.sellerAddresses[sender] = sellerId;
        marketplaceData.ar_sellers.push(sellerId);
    }
    
    function updateSeller(MarketplaceData storage marketplaceData, string ipfs) internal {
        marketplaceData.sellers[marketplaceData.sellerAddresses[msg.sender]].ipfs = ipfs;
    }
    
    function getItem(MarketplaceData storage marketplaceData, bytes32 itemId) internal view returns(string,uint,uint) {
        Item storage item = marketplaceData.items[itemId];
        require(item.exist);
        return (item.ipfs, item.price, item.quantity);
    }
    
    function addItem(MarketplaceData storage marketplaceData, string ipfs, uint price, uint quantity) internal returns(bytes32) {
        bytes32 itemId = keccak256(abi.encodePacked(msg.sender, ipfs, blockhash(block.number-1), now));
        
        require(!marketplaceData.items[itemId].exist);
        
        bytes32 sellerId = getSellerId(marketplaceData);
        marketplaceData.items[itemId] = Item({ipfs : ipfs, price : price, quantity : quantity, sellerId : sellerId, exist : true});
        marketplaceData.sellers[sellerId].items.push(itemId);
        
        return itemId;
    }
    
    function updateItem(MarketplaceData storage marketplaceData, bytes32 itemId, string ipfs, uint price, uint quantity) internal {
        Item storage item = marketplaceData.items[itemId];
         
        item.ipfs = ipfs;
        item.price = price;
        item.quantity = quantity;
    }
    
    function getBuyerId(MarketplaceData storage marketplaceData) internal view returns(bytes32) {
        marketplaceData.buyerAddresses[msg.sender];
    }
    
    function getBuyerByAddress(MarketplaceData storage marketplaceData) internal view returns(bytes32, string) {
        bytes32 buyerId = marketplaceData.buyerAddresses[msg.sender];
        Buyer storage buyer = marketplaceData.buyers[buyerId];
        require(buyer.exist);
        return (buyerId, buyer.ipfs);
    }
    
    function getBuyerById(MarketplaceData storage marketplaceData, bytes32 buyerId) internal view returns(address, string) {
        Buyer storage buyer = marketplaceData.buyers[buyerId];
        require(buyer.exist);
        return (buyer.addr, buyer.ipfs);
    }
    
    function registerBuyer(MarketplaceData storage marketplaceData, address sender, string ipfs) internal returns(bytes32) {
        bytes32 buyerId = keccak256(abi.encodePacked(sender, ipfs, blockhash(block.number-1), now));
        
        require(!marketplaceData.buyers[buyerId].exist);
        
        marketplaceData.buyers[buyerId] = Buyer({addr : sender, ipfs : ipfs, exist : true});
        marketplaceData.buyerAddresses[sender] = buyerId;
    }
    
    function updateBuyer(MarketplaceData storage marketplaceData, string ipfs) internal {
        Buyer storage buyer = marketplaceData.buyers[marketplaceData.buyerAddresses[msg.sender]];
        require(buyer.exist);
        buyer.ipfs = ipfs;
    }
    
    function buy(MarketplaceData storage marketplaceData, bytes32 itemId, uint tokens) internal returns(uint) {
        Item storage item = marketplaceData.items[itemId];
        require(item.exist);
        
        uint quantity = tokens.div(item.price);
        require(quantity > 0 &&item.quantity >= quantity);
        
        Seller storage seller = marketplaceData.sellers[item.sellerId];
        require(seller.exist);
        
        item.quantity = item.quantity.sub(quantity);
        
        return quantity;
    }
}
