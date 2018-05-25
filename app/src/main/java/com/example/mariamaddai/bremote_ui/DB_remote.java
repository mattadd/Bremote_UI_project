package com.example.mariamaddai.bremote_ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by mariam addai on 01-Apr-18.
 */

public class DB_remote extends SQLiteOpenHelper {

    public static final String TAG=DB_remote.class.getSimpleName();
    public static final String DB_NAME="b-remote.db";
    public static final int DB_VERSION=1;

    public static final String USER_TABLE="users";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_USERNAME="name";
    public static final String COLUMN_PASS="password";

    public static final String CREATE_TABLE_USERS="CREATE TABLE "+USER_TABLE +"("
            + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME +" TEXT,"
            + COLUMN_PASS +" TEXT);";

    public DB_remote(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ USER_TABLE);
        onCreate(db);
    }
    public void addUser(String name, String password){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(COLUMN_USERNAME, name);
        values.put(COLUMN_PASS, password);

        long id=db.insert(USER_TABLE,null, values);
        db.close();

        Log.d(TAG, "user inserted" + id);
    }

    public boolean getUser(String name, String pass){
        String selectQuery="Select * from "+ USER_TABLE + " where "+ COLUMN_USERNAME + " = '"+name+"' and "+COLUMN_PASS+" = '"+pass+"'";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

}
