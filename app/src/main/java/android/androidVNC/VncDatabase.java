/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Michael A. MacDonald
 *
 */
class VncDatabase extends SQLiteOpenHelper {
	static final int DBV_0_2_X = 9;
	static final int DBV_0_2_4 = 10;
	static final int DBV_0_4_7 = 11;
	static final int DBV_0_5_0 = 12;
	
	public final static String TAG = VncDatabase.class.toString();
	
	VncDatabase(Context context)
	{
		super(context,"VncDatabase",null,DBV_0_5_0);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(AbstractConnectionBean.GEN_CREATE);
		db.execSQL(MostRecentBean.GEN_CREATE);
		db.execSQL(MetaList.GEN_CREATE);
		db.execSQL(AbstractMetaKeyBean.GEN_CREATE);
		db.execSQL(SentTextBean.GEN_CREATE);
		
		db.execSQL("INSERT INTO "+MetaList.GEN_TABLE_NAME+" VALUES ( 1, 'DEFAULT')");
	}
	
	private void defaultUpgrade(SQLiteDatabase db)
	{
		Log.i(TAG, "Doing default database upgrade (drop and create tables)");
		db.execSQL("DROP TABLE IF EXISTS " + AbstractConnectionBean.GEN_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MostRecentBean.GEN_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MetaList.GEN_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + AbstractMetaKeyBean.GEN_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SentTextBean.GEN_TABLE_NAME);
		onCreate(db);		
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < DBV_0_2_X)
		{
			defaultUpgrade(db);
		}
		else {
			if (oldVersion == DBV_0_2_X)
			{
				Log.i(TAG, "Doing upgrade from 9 to 10");
				db.execSQL("ALTER TABLE " + AbstractConnectionBean.GEN_TABLE_NAME + " RENAME TO OLD_" +
						AbstractConnectionBean.GEN_TABLE_NAME);
				db.execSQL(AbstractConnectionBean.GEN_CREATE);
				db.execSQL("INSERT INTO " + AbstractConnectionBean.GEN_TABLE_NAME +
						" SELECT *, 0 FROM OLD_" + AbstractConnectionBean.GEN_TABLE_NAME);
				db.execSQL("DROP TABLE OLD_" + AbstractConnectionBean.GEN_TABLE_NAME);
				oldVersion = DBV_0_2_4;
			}
			if (oldVersion == DBV_0_2_4)
			{
				Log.i(TAG,"Doing upgrade from 10 to 11");
				db.execSQL("ALTER TABLE " + AbstractConnectionBean.GEN_TABLE_NAME + " ADD COLUMN " +AbstractConnectionBean.GEN_FIELD_USERNAME+" TEXT");
				db.execSQL("ALTER TABLE " + AbstractConnectionBean.GEN_TABLE_NAME + " ADD COLUMN " +AbstractConnectionBean.GEN_FIELD_SECURECONNECTIONTYPE+" TEXT");
				db.execSQL("ALTER TABLE " + MostRecentBean.GEN_TABLE_NAME + " ADD COLUMN " + MostRecentBean.GEN_FIELD_SHOW_SPLASH_VERSION + " INTEGER");
				db.execSQL("ALTER TABLE " + MostRecentBean.GEN_TABLE_NAME + " ADD COLUMN " + MostRecentBean.GEN_FIELD_TEXT_INDEX);
				oldVersion = DBV_0_4_7;
			}
			Log.i(TAG,"Doing upgrade from 11 to 12");
			// Haven't been using SentText before, primary key handling changed so drop and recreate it
			db.execSQL("DROP TABLE IF EXISTS " + SentTextBean.GEN_TABLE_NAME);
			db.execSQL(SentTextBean.GEN_CREATE);
			db.execSQL("ALTER TABLE " + AbstractConnectionBean.GEN_TABLE_NAME + " ADD COLUMN " +AbstractConnectionBean.GEN_FIELD_SHOWZOOMBUTTONS+" INTEGER DEFAULT 1");
			db.execSQL("ALTER TABLE " + AbstractConnectionBean.GEN_TABLE_NAME + " ADD COLUMN " +AbstractConnectionBean.GEN_FIELD_DOUBLE_TAP_ACTION+" TEXT");
		}
	}

}
