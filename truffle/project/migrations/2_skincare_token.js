var SkinCareToken = artifacts.require("./SkinCareToken.sol");
var SkinCareMarketplace = artifacts.require("./SkinCareMarketplace.sol");

module.exports = function(deployer) {
    deployer.deploy(SkinCareToken).then(function() {
        deployer.deploy(SkinCareMarketplace, SkinCareToken.address);
    });   
};
