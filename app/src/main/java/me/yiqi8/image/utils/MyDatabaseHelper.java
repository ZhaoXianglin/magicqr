package me.yiqi8.image.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASENAME = "qrcode.db";
	private static final String TABLENAME = "tab";
	public MyDatabaseHelper(Context context) {
		super(context, DATABASENAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " +  TABLENAME + " (" + 
				"code   	VARCHAR(50)		NOT NULL," +
				"url   	    VARCHAR(50)		NOT NULL," +
				"title      VARCHAR(25)     NOT NULL," +
				"content    VARCHAR(50)    NOT NULL)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLENAME;
		db.execSQL(sql);
		this.onCreate(db);
	}

}
