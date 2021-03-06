package net.kazhik.android.kmoney.db;

import java.math.BigDecimal;
import java.text.ParseException;

import net.kazhik.android.kmoney.bean.CreditCardTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class KmCreditCardTrns extends KmTable {
	public static final String TABLE_NAME = "km_creditcard_trns";
	private static final String CREATE_TABLE =
		    "CREATE TABLE " + TABLE_NAME + " (" +
	        "id INTEGER PRIMARY KEY," +
		    "transaction_date DATETIME," +
	        "expense REAL," +
	        "category_id INTEGER," +
	        "detail TEXT," +
			"image_uri TEXT," +
		    "internal INTEGER," +
	        "user_id INTEGER," +
	        "source INTEGER," +
	        "card_id INTEGER," +
	        "last_update_date DATETIME)";
	
    public KmCreditCardTrns(Context context) {
    	super(context);
    }
    public static void init(SQLiteDatabase db){
    	db.execSQL(CREATE_TABLE);

    }
	public static void upgrade(SQLiteDatabase db) {
		KmTable.upgrade(db, TABLE_NAME, CREATE_TABLE);
	}

    public CreditCardTransaction select(int id) throws ParseException {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TABLE_NAME);
		
		String[] columns = { "transaction_date", "category_id", "detail", "image_uri",
				"expense", "card_id", "internal", "user_id", "source" };
		String selection = "id = ?";
		String[] selectionArgs = {String.valueOf(id)};
		
		Cursor cursor = qb.query(this.db, columns, selection, selectionArgs, null,
				null, null, null);
		
		if (cursor.getCount() == 0) {
			return null;
		}
		
		cursor.moveToFirst();
		
		CreditCardTransaction trn = new CreditCardTransaction();
    	int idx = 0;
		trn.setTransactionDate(cursor.getString(idx++));
    	trn.setCategoryId(cursor.getInt(idx++));
    	trn.setDetail(cursor.getString(idx++));
		trn.setImageUri(cursor.getString(idx++));
    	trn.setExpense(new BigDecimal(cursor.getString(idx++)));
    	trn.setCardId(cursor.getInt(idx++));
    	trn.setInternal(cursor.getInt(idx++));
    	trn.setUserId(cursor.getInt(idx++));
    	trn.setSource(cursor.getInt(idx++));
    	
		cursor.close();
		    	
    	return trn;
    }
    public int insert(CreditCardTransaction trn) {
        ContentValues values = this.makeContentValues(trn);
        values.remove("income");
        values.put("card_id", trn.getCardId());
        
        return (int)this.db.insert(TABLE_NAME, null, values);
    	
    }
    public boolean update(CreditCardTransaction trn) {
        ContentValues values = this.makeContentValues(trn);
        values.remove("income");
        values.put("card_id", trn.getCardId());

        int updated = this.db.update(TABLE_NAME, values, "id = " + trn.getId(), null);
    	return (updated > 0);
    }
    public boolean delete(int id) {
    	int deleted = this.db.delete(TABLE_NAME, "id = " + id, null);
    	return (deleted > 0);
    }

}
