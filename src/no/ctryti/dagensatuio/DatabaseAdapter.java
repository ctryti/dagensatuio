package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DatabaseAdapter {

	private static final String DATABASE_NAME = "dagensatuio.db";
	private static final String TAG = "DatabaseAdapter";
	
	/* Database column names */
	public static final String PLACE = "place";
	public static final String DAY = "day";
	public static final String TYPE = "type";
	public static final String DESC = "desc";
	public static final String DISH = "dish";
	public static final String PERIOD = "period";
	public static final String SPAN = "span";
	public static final String YEAR = "year";
	public static final String GLUTEN = "has_gluten";
	public static final String LAKTOSE = "has_laktose";
	public static final String _ID = "_id";

	private static final HashMap<String, String> columnMap;
	static {
		
		//builder.setTables(DISH+" JOIN "+PLACE+" ON "+DISH+"."+PLACE+" = "+PLACE+"._id JOIN "+TYPE+" ON "+DISH+"."+TYPE+" = "+ TYPE+"._id JOIN "+DAY+" ON "+DISH+"."+DAY+" = "+DAY+"._id");
		
		//String query = "SELECT " + PLACE+"."+PLACE+", "+DAY+"."+DAY+", "+TYPE+"."+TYPE+", "+DISH+"."+DESC+", "+DISH+"."+PERIOD+" FROM "+DISH+" JOIN "+PLACE+" ON "+DISH+"."+PLACE+" = "+PLACE+"._id JOIN "+TYPE+" ON "+DISH+"."+TYPE+" = "+ TYPE+"._id JOIN "+DAY+" ON "+DISH+"."+DAY+" = "+DAY+"._id WHERE "+PLACE+"."+PLACE+"='"+place+"'";

		columnMap = new HashMap<String, String>();
		columnMap.put(PLACE, PLACE+"."+PLACE);
		columnMap.put(DAY, DAY+"."+DAY);
		columnMap.put(TYPE, TYPE+"."+TYPE);
		columnMap.put(DESC, DISH+"."+DESC);
		columnMap.put(PERIOD, DISH+"."+PERIOD);
		columnMap.put(GLUTEN, DISH+"."+GLUTEN);
		columnMap.put(LAKTOSE, DISH+"."+LAKTOSE);
	}
	
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private Context mCtx;
	private String[] days;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		private static final String TAG = "DatabaseAdapter$DatabaseHelper";
		private static final int DATABASE_VERSION = 6;

		/* Create statements */
		private static final String CREATE_TABLE_PLACES =
			"create table " + PLACE + " (\n" +
			"\t_id integer primary key autoincrement, \n" +
			"\t"+PLACE+" text not null unique\n" +
			");";
		private static final String CREATE_TABLE_DAY =
			"create table " + DAY + "(\n" +
			"\t_id integer primary key autoincrement, \n" +
			"\t"+DAY + " text not null unique\n" +
			");";
		private static final String CREATE_TABLE_TYPE =
			"create table " + TYPE + "(\n" +
			"\t_id integer primary key autoincrement, \n" +
			"\t"+TYPE + " text not null unique\n" +
			");";
		private static final String CREATE_TABLE_DISH =
			"create table " + DISH + "(\n" +
			"\t_id integer primary key autoincrement, \n" +
			"\t"+DESC   + " text not null, \n" +
			"\t"+PLACE  + " integer not null, \n" +
			"\t"+DAY    + " integer not null, \n" +
			"\t"+TYPE   + " integer not null, \n" +
			"\t"+PERIOD + " integer not null, \n" +
			"\t"+GLUTEN + " integer not null, \n" +
			"\t"+LAKTOSE+ " integer not null, \n" +
			"\tforeign key (" + PLACE + ") REFERENCES " + PLACE + ", \n" + //"(_id)," +
			"\tforeign key (" + DAY   + ") REFERENCES " + DAY   + ", \n" + //"(_id)," +
			"\tforeign key (" + TYPE  + ") REFERENCES " + TYPE  + ", \n" + //"(_id)," +
			"\tunique (" + DESC +", " + PERIOD + ", " + PLACE + ", " + DAY + ", \n" + TYPE + ")\n" +
			");";
	
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w(TAG, "Creating database (version: " + DATABASE_VERSION + ")");
			try {
				db.execSQL(CREATE_TABLE_PLACES);
				db.execSQL(CREATE_TABLE_DAY);
				db.execSQL(CREATE_TABLE_TYPE); 
				db.execSQL(CREATE_TABLE_DISH);
			} catch(Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy all old data");
			dropStatements(db);
			onCreate(db);
		}

		private void dropStatements(SQLiteDatabase db) {
			try {
				db.execSQL("DROP TABLE IF EXISTS " + DISH);
				db.execSQL("DROP TABLE IF EXISTS " + PLACE);
				db.execSQL("DROP TABLE IF EXISTS " + TYPE);
				db.execSQL("DROP TABLE IF EXISTS " + DAY);
			} catch(Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}

		
		//03-21 16:49:17.268: ERROR/AndroidRuntime(866): Caused by: android.database.sqlite.SQLiteException: near "dish": syntax error: , while compiling: SELECT place.place, day.day, type.type, dish.desc, dish.period, dish.has_gluten, dish.has_laktose FROM dish,type ,day,place WHERE (place.place='Frederikke kafé'dish.period=201011day.day=dish.daytype.type=dish.type)

		
		private void reCreateDatabase(SQLiteDatabase db) {
			Log.i(TAG, "Re-creating the database (version " + DATABASE_VERSION + ")");
			dropStatements(db);
			onCreate(db);
		}
	}

	public DatabaseAdapter(Context ctx) {
		mCtx = ctx;
		days = mCtx.getResources().getStringArray(R.array.weekdays);
	}

	public DatabaseAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void reCreateDatabase() {
		open();
		mDbHelper.reCreateDatabase(mDb);
		close();
	}	

	public Cursor fetchAllPlaces() {
		return mDb.query(PLACE, new String[] {_ID, PLACE}, null, null, null, null, null);
	}
	
	public long insert(ContentValues values) {
		boolean valid = true;
		if(!values.containsKey(DAY))
			valid = false;
		if(!values.containsKey(DESC))
			valid = false;
		if(!values.containsKey(PLACE))
			valid = false;
		if(!values.containsKey(TYPE))
			valid = false;
		if(!values.containsKey(PERIOD))
			valid = false;
		if(!values.containsKey(GLUTEN))
			valid = false;
		if(!values.containsKey(LAKTOSE))
			valid = false;
		if(!valid)
			throw new IllegalArgumentException();

		ContentValues cv = new ContentValues();
		long placeId, typeId, dayId;

		cv.put(PLACE, values.getAsString(PLACE));
		placeId = insertMinorValue(PLACE, cv);

		cv.clear();
		cv.put(TYPE, values.getAsString(TYPE));
		typeId = insertMinorValue(TYPE, cv);

		cv.clear();
		cv.put(DAY, days[values.getAsInteger(DAY)-2]);
		dayId = insertMinorValue(DAY, cv);

		cv.clear();
		cv.put(PLACE, placeId);
		cv.put(DAY, dayId);
		cv.put(TYPE, typeId);
		cv.put(DESC, values.getAsString(DESC));
		cv.put(GLUTEN, values.getAsInteger(GLUTEN));
		cv.put(LAKTOSE, values.getAsInteger(LAKTOSE));
		cv.put(PERIOD, values.getAsString(PERIOD));
				
		long rowId = mDb.insert(DISH, null, cv);
		if(rowId == -1) 
			Log.e(TAG, "Failed to insert: " + values.getAsString(PLACE)+", "+ values.getAsString(PERIOD)+ "," +values.getAsString(DAY) + ", " + values.getAsString(TYPE) + "," + values.getAsString(DESC));
		else	
			Log.i(TAG, "Insert: " + values.getAsString(PLACE)+", "+ values.getAsString(PERIOD)+ "," +values.getAsString(DAY) + ", " + values.getAsString(TYPE) + ", "+values.getAsString(DESC));
		//Log.i(TAG, "Inserted "+values.getAsString(KEY_PLACE)+","+values.getAsString(KEY_DAY));
		return rowId;
	}

	private long insertMinorValue(String key, ContentValues v) {
		long rowId;
		Cursor c = mDb.query(true, key, new String[] {_ID, key }, key + "='"+v.getAsString(key)+"'", null, null, null, null, null);

		if(c != null && c.getCount() > 0) {
			c.moveToFirst();
		} else {
			c.close();
			return mDb.insert(key, null, v);
		}
		rowId = c.getInt(c.getColumnIndex(_ID));
		c.close();
		return rowId;
	}
	
	private String mostRecentPeriod() {
		
		Cursor c = mDb.query(true, DISH, new String[] {PERIOD }, null, null, null, null, PERIOD, "1");
		if(c != null && c.getCount() > 0)
			c.moveToFirst();
		else 
			return "";
		
		String period = c.getString(c.getColumnIndex(PERIOD));
		return period;
	}
	
	public ArrayList<DinnerItem> getItems(String place, String period) {
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		//builder.setTables(DISH+" JOIN "+PLACE+" ON "+DISH+"."+PLACE+" = "+PLACE+"._id JOIN "+TYPE+" ON "+DISH+"."+TYPE+" = "+ TYPE+"._id JOIN "+DAY+" ON "+DISH+"."+DAY+" = "+DAY+"._id");
		
		open();
		
		builder.setProjectionMap(columnMap);
		builder.setTables(DISH+","+TYPE+","+DAY+","+PLACE);
		
		builder.appendWhere(DAY+"."+_ID+"="+DISH+"."+DAY);
		builder.appendWhere(" AND "+TYPE+"."+_ID+"="+DISH+"."+TYPE);

		if(place != null)
			builder.appendWhere(" AND "+columnMap.get(PLACE)+"='"+place+"' AND "+DISH+"."+PLACE + "="+PLACE+"."+_ID);
		
		if(period != null)
			builder.appendWhere(" AND "+DISH+"."+PERIOD+"="+period);
		else
			builder.appendWhere(" AND "+DISH+"."+PERIOD+"="+mostRecentPeriod());
		
		Cursor c = builder.query(mDbHelper.getReadableDatabase(),new String[]{ PLACE, DAY, TYPE, DESC, PERIOD, GLUTEN, LAKTOSE },null,null,null,null,null);
		
		System.out.println(builder.buildQuery(new String[]{ PLACE, DAY, TYPE, DESC, PERIOD, GLUTEN, LAKTOSE },null,null,null,null,null, null));
		ArrayList<DinnerItem> list = new ArrayList<DinnerItem>();
		DinnerItem item;
		
		String a[] = c.getColumnNames();
		
		for(String s : a)
			System.out.println(s);
		
		
		if(c.moveToFirst()) {
			String desc, type;
			boolean laktose, gluten;
			int day;
			do {
				desc = c.getString(c.getColumnIndex(DESC));
				period = c.getString(c.getColumnIndex(PERIOD));
				day = c.getInt(c.getColumnIndex(DAY));
				type = c.getString(c.getColumnIndex(TYPE));
				laktose = c.getString(c.getColumnIndex(LAKTOSE)).equals("1") ? true : false;
				gluten = c.getString(c.getColumnIndex(GLUTEN)).equals("1") ? true : false;

				item = new DinnerItem(place, day, type, desc, period, gluten, laktose);
				list.add(item);
				
			} while(c.moveToNext());
		}
		close();
		c.close();
		return list;
	}

	public ArrayList<DinnerItem> getAllFromPlace(String place) {
		open();
		//Cursor c = mDb.query(KEY_DISH, new String[] {KEY_ROWID, KEY_PLACE, KEY_DAY, KEY_PERIOD, KEY_TYPE, KEY_DESC }, KEY_PLACE+"='2'", null, null, null, null);
		String query = "SELECT " + PLACE+"."+PLACE+", "+DAY+"."+DAY+", "+TYPE+"."+TYPE+", "+DISH+"."+DESC+", "+DISH+"."+PERIOD+" FROM "+DISH+" JOIN "+PLACE+" ON "+DISH+"."+PLACE+" = "+PLACE+"._id JOIN "+TYPE+" ON "+DISH+"."+TYPE+" = "+ TYPE+"._id JOIN "+DAY+" ON "+DISH+"."+DAY+" = "+DAY+"._id WHERE "+PLACE+"."+PLACE+"='"+place+"'";
		//String query = "SELECT * FROM "+KEY_DISH+" JOIN "+KEY_PLACE+" ON "+KEY_DISH+"."+KEY_PLACE+" = "+KEY_PLACE+"._id JOIN "+KEY_TYPE+" ON "+KEY_DISH+"."+KEY_TYPE+" = "+ KEY_TYPE+"._id JOIN "+KEY_DAY+" ON "+KEY_DISH+"."+KEY_DAY+" = "+KEY_DAY+"._id WHERE "+KEY_PLACE+"."+KEY_PLACE+"='"+place+"'";
		Cursor c = mDb.rawQuery(query, null);
		
		ArrayList<DinnerItem> list = new ArrayList<DinnerItem>();
		DinnerItem item;
		
		if(c.moveToFirst()) {
			String desc, period, type;
			int day;
			do {
				desc = c.getString(3);
				period = c.getString(4);
				day = c.getInt(1);
				type = c.getString(2);
				
				item = new DinnerItem(place, day, type, desc, period, false, false);
				list.add(item);
				
			} while(c.moveToNext());
		}
		close();
		c.close();
		return list;
	}
}
