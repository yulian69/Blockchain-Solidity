
const SkinCareToken = artifacts.require('SkinCareToken');
const SkinCareMarketplace = artifacts.require('SkinCareMarketplace');
const SafeMathTest = artifacts.require('SafeMathTest');

contract('SkinCareMarketplace', function (accounts) {
	let token;
    let marketplace; 

    function getBytes(str) {
        var res = "";
        for (var i = 0; i < str.length; i++) {
            var hex = str.charCodeAt(i).toString(16);
            hex = (hex.length===1)?"0"+hex:hex;
            res += hex;
        }
        return res;
    }

    beforeEach(async function () {
        token = await SkinCareToken.new();
        marketplace = await SkinCareMarketplace.new(token.address);
    });
	
	describe("SafeMath Functionality Tests",() => {
   	    it('should test SafeMath', async function() {
			let safeMathTest = await SafeMathTest.new();
			safeMathTest.testSafeMath();
			assert(true);			
    	});
    });
	
	describe("Ownable Functionality Tests",() => {
		it('should not send ethers', async function() {
			let res = false;
            try {
                await marketplace.send(10, {from: accounts[0]});              
            } catch (err) {
                res = true;
            }
            assert(res);			
    	});  
   	    it('should set owner on create', async function() {
            let owner = await marketplace.owner(); 
        	assert.equal(owner, accounts[0]);
    	});
        
        it('should transfer ownership', async function() {
            await marketplace.transferOwnership(accounts[1]); 
            let owner = await marketplace.owner(); 
        	assert.equal(owner, accounts[1]);
    	});

        it('should not transfer ownership', async function() {
            let res = false;
            try {
                await token.transferOwnership(accounts[2], {from: accounts[1]});                
            } catch (err) {
                res = true;
            }
            assert(res);
    	});
    });

    describe("Token Functionality Tests",() => {
		it('should get total supply', async function() {
			assert.equal(await token.totalSupply(), 100000000);		
    	}); 

		it('should appove token transfer', async function() {
			await token.approve(accounts[1], 100, {from: accounts[0]});
			assert.equal(await token.allowance(accounts[0],accounts[1]), 100);		
    	}); 
		it('should transfer contract token', async function() {
			await token.transfer(token.address, 100, {from: accounts[0]});
			assert.equal(await token.balanceOf(token.address),100);
			
			await token.transferAnyERC20Token(token.address, 100, {from: accounts[0]});
			assert.equal(await token.balanceOf(token.address),0);		
    	}); 
		it('should not send ethers', async function() {
			let res = false;
            try {
                await token.send(10, {from: accounts[0]});              
            } catch (err) {
                res = true;
            }
            assert(res);			
    	});
   	    it('should set token address on create', async function() {
            let tokenAddress = await marketplace.getTokenAddress(); 
        	assert.equal(token.address, tokenAddress);
    	});

        it('should set token address', async function() {
            await marketplace.setTokenAddress(accounts[1]); 
            let tokenAddress = await marketplace.getTokenAddress(); 
        	assert.equal(accounts[1], tokenAddress);
    	});

        it('should not set token address', async function() {
            try {
                await marketplace.setTokenAddress(accounts[1], {from: accounts[2]});                 
            	assert(false);
            } catch (err) {
                assert(true);
            }
    	});

        it('should transfer 400 of 1000 tokens', async function() {
            await token.transfer(marketplace.address,1000);
            await marketplace.transferTokens(accounts[1],400);
            let marketplaceTokens = await token.balanceOf(marketplace.address);
            let accountTokens = await token.balanceOf(accounts[1]);
        	assert.equal(marketplaceTokens, 600);
            assert.equal(accountTokens, 400);
    	});

        it('should not transfer any tokens', async function() {
            let res = false;
            try {
                await token.transfer(marketplace.address,1000);
                await marketplace.transferTokens(accounts[1],400, {from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res);
            
    	});

        it('should transfer all available tokens to the owner', async function() {
            await token.transfer(marketplace.address,1000);
            let ownerTokens = await token.balanceOf(accounts[0]);
            
            await marketplace.transferAllTokens();
            let marketplaceTokens = await token.balanceOf(marketplace.address);
            let ownerTokensNew = await token.balanceOf(accounts[0]);
        	assert.equal(marketplaceTokens, 0);
            assert.equal(ownerTokensNew, ownerTokens.toNumber()+1000);
    	});

        it('should not transfer any tokens to the owner', async function() {
            let res = false;
            try {
                await token.transfer(marketplace.address,1000);
                await marketplace.transferAllTokens({from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res);
            
    	});

        it('should not transfer more than available tokens', async function() {
            let res = false;
            try {
                await token.transfer(marketplace.address,1000);
                await marketplace.transferTokens(accounts[1],4000);
            } catch (err) {
                res = true;
            }
            assert(res);
            
    	});


        it('should kill contract an transfer all available tokens to the owner', async function() {
            await token.transfer(marketplace.address,1000);
            let ownerTokens = await token.balanceOf(accounts[0]);
            
            await marketplace.kill();
            let marketplaceTokens = await token.balanceOf(marketplace.address);
            let ownerTokensNew = await token.balanceOf(accounts[0]);
        	assert.equal(marketplaceTokens, 0);
            assert.equal(ownerTokensNew, ownerTokens.toNumber()+1000);
    	});

        it('should not kill the contract', async function() {
            let res = false;
            try {
                await marketplace.kill({from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res);     
    	});        
    });
  
    
    describe("Sellers Functionality Tests",() => {
   	    it('should add seller', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            
            let marketplaceTokens = await token.balanceOf(marketplace.address);
            let sellerTokens = await token.balanceOf(accounts[1]);
            let sellers = await marketplace.getSellers();

            let seller = await marketplace.getSellerByAddress({from: accounts[1]});           
 
            assert.equal(marketplaceTokens, 20000);
            assert.equal(sellerTokens, 0);
            assert.equal(sellers.length, 1);
            assert.equal(seller[1], "ipfs");
    	});

        it('should not add existing seller', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
                     
 
            let res = false;
            try {
                await token.transfer(accounts[1],20000);
                await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res); 
    	});

        it('should get seller by address', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let sellers = await marketplace.getSellers();

            let seller = await marketplace.getSellerByAddress({from: accounts[1]}); 
            assert.equal(sellers[0],seller[0]);
    	});

        it('should get seller by id', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let sellers = await marketplace.getSellers();

            let seller = await marketplace.getSellerById(sellers[0]); 
            assert.equal(seller[1],"ipfs");
    	});

        it('should update seller', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
                        
            await marketplace.updateSeller("ipfs-new",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});
            assert.equal(seller[1],"ipfs-new");
            assert(true); 
    	});
      
        it('should not update seller', async function() {
            let res = false;
            try {
                await marketplace.updateSeller("ipfs-new",{from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res); 
    	});

        it('should get sellers', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs1")+"01",{from: accounts[1]});

            await token.transfer(accounts[2],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs2")+"01",{from: accounts[2]});

            let sellers = await marketplace.getSellers();

            let seller1 = await marketplace.getSellerById(sellers[0]);
            let seller2 = await marketplace.getSellerById(sellers[1]); 

            assert.equal(sellers.length,2);
            assert.equal(seller1[1],"ipfs1");
            assert.equal(seller2[1],"ipfs2");
    	});

    });

    describe("Items Functionality Tests",() => {
   	    it('should add item', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
            let item = await marketplace.getItem(items[0]);
               
            assert.equal(item[0], "ipfs");
            assert.equal(item[1], 1000);
            assert.equal(item[2], 100);
    	});

        it('should not add item', async function() {
            let res = false;
            try {
                await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res); 
            
    	});

        it('should update item', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});
            
            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);            
            
            await marketplace.updateItem(items[0], "ipfs-new", 10000, 1000, {from: accounts[1]});  
            let item = await marketplace.getItem(items[0]);             

            assert.equal(item[0], "ipfs-new");
            assert.equal(item[1], 10000);
            assert.equal(item[2], 1000);
    	});

       it('should not update item', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});
           
            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);            
                        
            let res = false;
            try {
                await marketplace.updateItem(items[0], "ipfs-new", 10000, 1000, {from: accounts[2]});
            } catch (err) {
                res = true;
            }
            assert(res); 
            
    	});

        it('should not update item', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});
           
            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);            
                        
            let res = false;
            try {
                await marketplace.updateItem("0x01", "ipfs-new", 10000, 1000, {from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res);             
    	}); 

        it('should get items', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});
            
            await marketplace.addItem("ipfs1", 1001, 101, {from: accounts[1]});
            await marketplace.addItem("ipfs2", 1002, 102, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);  
            
            let item1 = await marketplace.getItem(items[0]);  
            let item2 = await marketplace.getItem(items[1]);          
                        
            assert.equal(item1[0], "ipfs1");
            assert.equal(item1[1], 1001);
            assert.equal(item1[2], 101); 
            assert.equal(item2[0], "ipfs2");
            assert.equal(item2[1], 1002);
            assert.equal(item2[2], 102);
    	}); 
    });

    describe("Buy Tests",() => {
   	    it('should register and buy item', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
             
            await token.transfer(accounts[2],2000);

            let marketplaceTokens = await token.balanceOf(marketplace.address);
            let sellerTokens = await token.balanceOf(accounts[1]);
            let buyerTokens = await token.balanceOf(accounts[2]);

            await token.approveAndCall(marketplace.address,2000,items[0]+getBytes("ipfs")+"02",{from: accounts[2]});

            let marketplaceTokens2 = await token.balanceOf(marketplace.address);
            let sellerTokens2 = await token.balanceOf(accounts[1]);
            let buyerTokens2 = await token.balanceOf(accounts[2]);
               
            let item = await marketplace.getItem(items[0]);
            let buyer = await marketplace.getBuyerByAddress({from: accounts[2]});
            
            assert.equal(buyer[1],"ipfs");
            assert.equal(item[2],98);
            assert.equal(marketplaceTokens2-marketplaceTokens,100);
            assert.equal(sellerTokens2-sellerTokens,1900);
            assert.equal(buyerTokens-buyerTokens2,2000);
    	});

        it('should buy item', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
            
            await token.transfer(accounts[2],10000);

            let marketplaceTokens = await token.balanceOf(marketplace.address);
            let sellerTokens = await token.balanceOf(accounts[1]);
            let buyerTokens = await token.balanceOf(accounts[2]);

            await token.approveAndCall(marketplace.address,2000,items[0]+getBytes("ipfs")+"02",{from: accounts[2]});
            await token.approveAndCall(marketplace.address,3000,items[0]+"03",{from: accounts[2]});

            let marketplaceTokens2 = await token.balanceOf(marketplace.address);
            let sellerTokens2 = await token.balanceOf(accounts[1]);
            let buyerTokens2 = await token.balanceOf(accounts[2]);
               
            let item = await marketplace.getItem(items[0]);
            
            assert.equal(item[2],95);
            assert.equal(marketplaceTokens2-marketplaceTokens,250);
            assert.equal(sellerTokens2-sellerTokens,4750);
            assert.equal(buyerTokens-buyerTokens2,5000);
    	});

        it('should not buy item (no available tokens)', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
              
            await token.transfer(accounts[2],1000);
            let res = false;
            try {
                await token.approveAndCall(marketplace.address,2000,items[0]+getBytes("ipfs")+"02",{from: accounts[2]});
            } catch (err) {
                res = true;
            }
            assert(res);
    	});

        it('should not buy item (no avalable items)', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 2, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
              
            await token.transfer(accounts[2],3000);

            let res = false;
            try {
                await token.approveAndCall(marketplace.address,3000,items[0]+getBytes("ipfs")+"02",{from: accounts[2]});
            } catch (err) {
                res = true;
            }
            assert(res);
    	});

        it('should not buy item (not registered buyer)', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
            
            await token.transfer(accounts[2],10000);

            let marketplaceTokens = await token.balanceOf(marketplace.address);
            let sellerTokens = await token.balanceOf(accounts[1]);
            let buyerTokens = await token.balanceOf(accounts[2]);
  
            let res = false;
            try {
                await token.approveAndCall(marketplace.address,3000,items[0]+"03",{from: accounts[2]});
            } catch (err) {
                res = true;
            }
            assert(res);
    	});        
    });

    describe("Buyer Tests",() => {
   	    it('should update buyer', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
             
            await token.transfer(accounts[2],2000);

            await token.approveAndCall(marketplace.address,2000,items[0]+getBytes("ipfs")+"02",{from: accounts[2]});

            await marketplace.updateBuyer("ipfs-new", {from: accounts[2]});
            let buyer = await marketplace.getBuyerByAddress({from: accounts[2]});
            
            assert.equal(buyer[1],"ipfs-new");
    	});

        it('should get buyer by address', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
             
            await token.transfer(accounts[2],2000);

            await token.approveAndCall(marketplace.address,2000,items[0]+getBytes("ipfs")+"02",{from: accounts[2]});

            let buyer = await marketplace.getBuyerByAddress({from: accounts[2]});
            
            assert.equal(buyer[1],"ipfs");
    	});

        it('should get buyer by id', async function() {
            await token.transfer(accounts[1],20000);
            await token.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            let seller = await marketplace.getSellerByAddress({from: accounts[1]});

            await marketplace.addItem("ipfs", 1000, 100, {from: accounts[1]});

            let items = await marketplace.getItems(seller[0]);
             
            await token.transfer(accounts[2],2000);

            await token.approveAndCall(marketplace.address,2000,items[0]+getBytes("ipfs")+"02",{from: accounts[2]});

            let buyer1 = await marketplace.getBuyerByAddress({from: accounts[2]});
            let buyer2 = await marketplace.getBuyer(buyer1[0]);
            
            assert.equal(accounts[2],buyer2[0]);
            assert.equal(buyer1[1],buyer2[1]);
    	});
    });

    describe("Receive approval Tests",() => {
   	    it('should fail (request not from token)', async function() {
            let res = false;
            try {
                await token.transfer(accounts[1],20000);
                await marketplace.approveAndCall(marketplace.address,20000,"0x"+getBytes("ipfs")+"01",{from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res);
    	});
        
        it('should fail (no data sent)', async function() {
            let res = false;
            try {
                await token.transfer(accounts[1],20000);
                await token.approveAndCall(marketplace.address,2000,"",{from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res);
    	});

        it('should fail (invalid parameter)', async function() {
            let res = false;
            try {
                await token.transfer(accounts[1],20000);
                await token.approveAndCall(marketplace.address,2000,"0x04",{from: accounts[1]});
            } catch (err) {
                res = true;
            }
            assert(res);
    	});
    });
});
