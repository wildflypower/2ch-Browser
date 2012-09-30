package com.vortexwolf.dvach.db;

import java.io.File;

import com.vortexwolf.dvach.services.SqlCreateTableScriptBuilder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DvachSqlHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "dvach.db";
	private static final int DATABASE_VERSION = 2;
	
	public static final String TABLE_HISTORY = "history";
	public static final String TABLE_FAVORITES = "favorites";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_CREATED = "created";
	

	private static final SqlCreateTableScriptBuilder mHistorySqlBuilder = new SqlCreateTableScriptBuilder(TABLE_HISTORY);
	private static final SqlCreateTableScriptBuilder mFavoritesSqlBuilder = new SqlCreateTableScriptBuilder(TABLE_FAVORITES);
	
	public DvachSqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createHistoryTableSql = mHistorySqlBuilder
				.addPrimaryKey(COLUMN_ID)
				.addColumn(COLUMN_TITLE, "text", false)
				.addColumn(COLUMN_URL, "text", false)
				.addColumn(COLUMN_CREATED, "long", false)
				.toSql();
		db.execSQL(createHistoryTableSql);
		
		String createFavoritesTableSql = mFavoritesSqlBuilder
				.addPrimaryKey(COLUMN_ID)
				.addColumn(COLUMN_TITLE, "text", false)
				.addColumn(COLUMN_URL, "text", false)
				.toSql();
		db.execSQL(createFavoritesTableSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		drop(db);
		onCreate(db);
	}
	
	public void drop(SQLiteDatabase db){
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
	}
}
