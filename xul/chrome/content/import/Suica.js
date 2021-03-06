Components.utils.import("resource://gre/modules/NetUtil.jsm");

function Suica(db) {
    AbstractImport.call(this, db, "Suica");
}
Suica.prototype = Object.create(AbstractImport.prototype);


Suica.prototype.importDb = function (name, suicaHtmlFile, userId, importCallback) {
    var emoneyId;
    function onLoadImportConf(sourceType) {
        function onFileOpen(inputStream, status) {
            function insertCallback() {
                var importHistory = {
                    "user_id": userId,
                    "source_type": sourceType,
                    "source_name": name,
                    "source_url": suicaHtmlFile.path,
                    "period_from": newRecordArray[0]["transactionDate"],
                    "period_to": newRecordArray[newRecordArray.length - 1]["transactionDate"]
                };
                this.mDb.importHistory.insert(importHistory, importCallback.bind(this));
            }
            var strBuff = "";
            var parser = new DOMParser();
            var htmlDoc;
            var elemTable;
            var rowData;
        
            if (!Components.isSuccessCode(status)) {
                return;
            }
        
            strBuff = NetUtil.readInputStreamToString(inputStream,
                                                      inputStream.available(),
                                                      {"charset": "Shift_JIS"}
                                                      );
        
            htmlDoc = parser.parseFromString(strBuff, "text/html");
        
            elemTable = htmlDoc.getElementsByClassName("grybg01");
            if (elemTable.length === 0) {
                return;
            }
        
            rowData = elemTable[0].getElementsByTagName("tr");
            if (rowData.length === 0) {
                return;
            }
            var columnData;
            var prevBalance = -1;
            var balance = 0;
            var newRecordArray = [];
            for (var i = rowData.length - 1; i >= 0; --i) {
                columnData = rowData[i].getElementsByClassName("whtbg");
                if (columnData.length === 0) {
                    continue;
                }
        
                var rec = {
                    "transactionDate": "",
                    "income": 0,
                    "expense": 0,
                    "categoryId": 0,
                    "detail": "",
                    "userId": userId,
                    "moneyId": emoneyId,
                    "internal": 0,
                    "source": sourceType,
                };
                // 月日を年月日に変換
                // ファイル内に年データがないので、データは1年以内のものと想定
                var today = new Date();
                var year = today.getFullYear();
                var monthday = (columnData[0].textContent).split("/");
                if (monthday[0] > today.getMonth() + 1) {
                    --year;
                }
                rec["transactionDate"] = year + "-" + monthday[0] + "-" + monthday[1];
        
                rec["detail"] = columnData[1].textContent.trim();
        
                balance = parseFloat(columnData[5].textContent.replace(/[^\d.]+/g, ""));
        
                if (rec["detail"] === "繰") {
                    prevBalance = balance;
                    continue;
                }
                // 残高が減っていれば支出、増えていれば収入とする
                if (prevBalance > balance) {
                    // 第5カラムがある場合は電車
                    if (columnData[4].textContent != "") {
                        rec["detail"] += ":";
                        rec["detail"] += columnData[2].textContent.trim();
                        rec["detail"] += " - ";
                        rec["detail"] += columnData[3].textContent.trim();
                        rec["detail"] += ":";
                        rec["detail"] += columnData[4].textContent.trim();
                        rec["categoryId"] = this.getItemId("交通費");
                    } else {
                        rec["categoryId"] = this.getItemId(rec["detail"]);
                    }
                    rec["income"] = 0;
                    rec["expense"] = prevBalance - balance;
                } else {
                    rec["categoryId"] = this.getItemId("チャージ");
                    rec["internal"] = 1;
                    rec["income"] = balance - prevBalance;
                    rec["expense"] = 0;
                }
        
                prevBalance = balance;
        
                newRecordArray.push(rec);
            }
            this.mDb.emoneyTrns.import(newRecordArray, insertCallback.bind(this));
            
        }
        if (this.importItemArray.length === 0) {
            km_alert(km_getLStr("error.title"),
                     km_getLStr("error.import.noConf"));
            return;
        }
        NetUtil.asyncFetch(suicaHtmlFile, onFileOpen.bind(this));
    }
    emoneyId = this.mDb.emoneyInfo.getMoneyId(this.type, userId);
    this.loadImportConf(userId, null, onLoadImportConf.bind(this))
    
};