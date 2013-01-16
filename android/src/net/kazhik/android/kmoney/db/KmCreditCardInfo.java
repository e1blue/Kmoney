package net.kazhik.android.kmoney.db;

import java.util.ArrayList;
import java.util.List;

import net.kazhik.android.kmoney.bean.CreditCardInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class KmCreditCardInfo extends KmTable {
	private static final String CREATE_TABLE =
		    "CREATE TABLE km_creditcard_info (" +
	        "id INTEGER PRIMARY KEY," +
	        "name TEXT," +
	        "user_id INTEGER)";
	private static final String TABLE_NAME = "km_creditcard_info";

    public KmCreditCardInfo(Context context) {
    	super(context);
    }
    public static void init(SQLiteDatabase db, String[] cards){
    	db.execSQL(CREATE_TABLE);
    	
        ContentValues initialValues = new ContentValues();
        
        for (int i = 0; i < cards.length; i++) {
        	for (int j = 0; j < 2; j++) {
                initialValues.put("name", cards[i]);
                initialValues.put("user_id", j + 1);
                db.insert(TABLE_NAME, null, initialValues);
        	}
        }
    }
	public List<CreditCardInfo> getCreditCardList() {
		return this.getCreditCardList(0);
	}
	public List<CreditCardInfo> getCreditCardList(int max) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TABLE_NAME);

		String[] columns = { "id", "name", "user_id" };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		String limit = (max == 0)? null: Integer.toString(max);
		
		Cursor cursor = qb.query(this.db, columns, selection, selectionArgs, null,
				null, sortOrder, limit);
		
		List<CreditCardInfo> cardList = new ArrayList<CreditCardInfo>();
		
		if (cursor == null) {
			return cardList;
		}
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			CreditCardInfo info = new CreditCardInfo();
			int idx = 0;
			info.setId(cursor.getInt(idx++));
			info.setName(cursor.getString(idx++));
			info.setUser_id(cursor.getInt(idx++));
			cardList.add(info);
			
			cursor.moveToNext();
		}
		cursor.close();
		
		return cardList;
	}
}
