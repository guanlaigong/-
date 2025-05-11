package com.example.gong0427;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ExamDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_RESULTS = "results";
    public static final String COL_ID = "id";
    public static final String COL_QUESTIONS = "questions";
    public static final String COL_ANSWERS = "answers";
    public static final String COL_SCORE = "score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_RESULTS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_QUESTIONS + " TEXT,"
                + COL_ANSWERS + " TEXT,"
                + COL_SCORE + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
    }
}