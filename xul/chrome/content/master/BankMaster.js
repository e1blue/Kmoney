"use strict";

function BankMaster() {
  this.mDb = null;
  this.mTree = new TreeViewController("km_tree_master_bank");
  this.listeners = [];
};
BankMaster.prototype.initialize = function(db) {
  this.mDb = db;
  
  this.mTree.init(this, this.load.bind(this));

  this.listeners['km_tree_master_bank.select'] = this.onSelect.bind(this);
  $$('km_tree_master_bank').addEventListener("select",
    this.listeners['km_tree_master_bank.select']);

  this.load();
};
BankMaster.prototype.terminate = function() {
  $$('km_tree_master_bank').removeEventListener("select",
    this.listeners['km_tree_master_bank.select']);
};

BankMaster.prototype.load = function() {
    function loadCallback(records, columns) {
        this.mTree.populateTableData(records, columns);
        this.mTree.showTable(true);
    }
    km_debug("BankMaster.load");
    this.mDb.bankInfo.loadMaster(loadCallback.bind(this));
  
};
BankMaster.prototype.addRecord = function() {
    function onCompleted() {
        this.load();
    }
    var params = {
        "name": $$('km_textbox_name').value,
        "userId": $$('km_list_user').value
    };
    
    this.mDb.bankInfo.insert(params, onCompleted.bind(this));
}
BankMaster.prototype.updateRecord = function() {
    function onCompleted() {
        this.load();
        this.mTree.ensureRowIsVisible("master_bank_id", id);
    }
    var id = this.mTree.getSelectedRowValue("master_bank_id");
    var params = {
        "name": $$('km_textbox_name').value,
        "userId": $$('km_list_user').value
    };
    
    this.mDb.bankInfo.update(id, params, onCompleted.bind(this));
};

BankMaster.prototype.deleteRecord = function() {
    function onCompleted() {
        this.load();
    }
    var id = this.mTree.getSelectedRowValue("master_bank_id");
    this.mDb.bankInfo.delete(id, onCompleted.bind(this));

};

BankMaster.prototype.onSelect = function() {
    $$('km_textbox_name').value = this.mTree.getSelectedRowValue("master_bank_name");
    $$('km_list_user').value = this.mTree.getSelectedRowValue("master_bank_userid");
 
};



