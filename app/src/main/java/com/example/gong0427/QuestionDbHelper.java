package com.example.gong0427;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuestionDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "questions.db";
    private static final int DATABASE_VERSION = 2;

    public QuestionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE questions ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question TEXT NOT NULL);";

        final String SQL_CREATE_OPTIONS_TABLE = "CREATE TABLE options ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question_id INTEGER NOT NULL, " + // 外键
                "option_text TEXT NOT NULL, " +
                "is_correct INTEGER DEFAULT 0, " + // 是否正确答案
                "FOREIGN KEY(question_id) REFERENCES questions(_id) ON DELETE CASCADE" +
                ");";

        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_OPTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS options");
        db.execSQL("DROP TABLE IF EXISTS questions");
        onCreate(db);
    }
}