"use strict";

var masterData;

function MasterData() {
    
    this.userMaster = null;
    this.itemMaster = null;
    this.cardMaster = null;
    this.bankMaster = null;
    this.emoneyMaster = null;
  
};

function openMasterDataDialog() {
    masterData = new MasterData();
    masterData.initialize(window.arguments[0]);
}

MasterData.prototype.initialize = function(db) {
    
    this.userMaster = new UserMaster();
    this.userMaster.initialize(db);
    this.userMaster.load();
    this.itemMaster = new ItemMaster(db);
    this.itemMaster.initialize(db);
    /*
    this.cardMaster = new CardMaster(db);
    this.cardMaster.initialize(db);
    this.bankMaster = new BankMaster(db);
    this.bankMaster.initialize(db);
    this.emoneyMaster = new EMoneyMaster(db);
    this.emoneyMaster.initialize(db);
    */  
};
