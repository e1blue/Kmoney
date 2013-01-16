package net.kazhik.android.kmoney.db;

import java.util.ArrayList;
import java.util.List;

import net.kazhik.android.kmoney.bean.TransactionView;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class KmvTransactions extends KmTable {
	private static final String CREATE_VIEW =
			"CREATE VIEW kmv_transactions AS "
			+ "SELECT "
			+ "transaction_date,"
			+ "detail,"
			+ "expense, "
			+ "type, "
			+ "id "
			+ "FROM ("
			+ "SELECT "
			+ "transaction_date,"
			+ "detail,"
			+ "income - expense AS expense, "
			+ "'" + TransactionView.EMONEY + "' AS type, "
			+ "id "
			+ "FROM km_emoney_trns "
			+ "UNION "
			+ "SELECT "
			+ "transaction_date,"
			+ "detail,"
			+ "expense, "
			+ "'" + TransactionView.CREDITCARD + "' AS type, "
			+ "id "
			+ "FROM km_creditcard_trns "
			+ "UNION "
			+ "SELECT "
			+ "transaction_date,"
			+ "detail,"
			+ "income - expense AS expense, "
			+ "'" + TransactionView.CASH + "' AS type, "
			+ "id "
			+ "FROM km_realmoney_trns "
			+ "UNION "
			+ "SELECT "
			+ "transaction_date,"
			+ "detail,"
			+ "income - expense AS expense, "
			+ "'" + TransactionView.BANK + "' AS type, "
			+ "id "
			+ "FROM km_bank_trns)";
	private static final String TABLE_NAME = "kmv_transactions";

	public KmvTransactions(Context context) {
		super(context);
	}

	public static void init(SQLiteDatabase db) {
		db.execSQL(CREATE_VIEW);

	}
    public List<String> getDetailHistory(String type, int max) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TABLE_NAME);

		String[] columns = { "detail", "count(*) as cnt" };
		String selection = "type = ?";
		String[] selectionArgs = {type};
		String sortOrder = "cnt desc";
		String groupBy = "detail";
		String limit = (max == 0)? null: Integer.toString(max);
		
		Cursor cursor = qb.query(this.db, columns, selection, selectionArgs, groupBy,
				null, sortOrder, limit);
		
    	List<String> detailList = new ArrayList<String>();
		
		if (cursor == null) {
			return detailList;
		}
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			detailList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		
    	return detailList;
    }
	
	public List<TransactionView> getList(int year, int month) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TABLE_NAME);
		
		String[] columns = { "transaction_date", "detail", "expense", "type", "id" };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		String limit = null;
		
		Cursor cursor = qb.query(this.db, columns, selection, selectionArgs, null,
				null, sortOrder, limit);
		
		List<TransactionView> trnsList = new ArrayList<TransactionView>();
		
		if (cursor == null) {
			return trnsList;
		}
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			TransactionView info = new TransactionView();
			int idx = 0;
			info.setTransactionDate(cursor.getString(idx++));
			info.setDetail(cursor.getString(idx++));
			info.setExpense(cursor.getString(idx++));
			info.setType(cursor.getString(idx++));
			info.setId(cursor.getInt(idx++));
			trnsList.add(info);
			
			cursor.moveToNext();
		}
		cursor.close();
		
		return trnsList;		
	}

}
