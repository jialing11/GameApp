package com.game.simonsaysgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class HighScoreDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HighScores.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HighScoreEntry.TABLE_NAME + " (" +
                    HighScoreEntry._ID + " INTEGER PRIMARY KEY," +
                    HighScoreEntry.COLUMN_NAME_NAME + " TEXT," +
                    HighScoreEntry.COLUMN_NAME_SCORE + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HighScoreEntry.TABLE_NAME;

    public HighScoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean addHighScore(String name1, int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HighScoreEntry.COLUMN_NAME_NAME, name1);
        values.put(HighScoreEntry.COLUMN_NAME_SCORE, score);

        long newRowId = db.insert(HighScoreEntry.TABLE_NAME, null, values);

        return newRowId != -1;
    }

    public Cursor getAllHighScores() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                HighScoreEntry._ID,
                HighScoreEntry.COLUMN_NAME_NAME,
                HighScoreEntry.COLUMN_NAME_SCORE
        };

        String sortOrder =
                HighScoreEntry.COLUMN_NAME_SCORE + " DESC";

        return db.query(
                HighScoreEntry.TABLE_NAME,         // The table to query
                projection,                       // The array of columns to return (pass null to get all)
                null,                    // The columns for the WHERE clause
                null,                // The values for the WHERE clause
                null,                    // don't group the rows
                null,                     // don't filter by row groups
                sortOrder                      // The sort order
        );
    }

    public static class HighScoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "high_scores";
        public static final String COLUMN_NAME_NAME = "name1";
        public static final String COLUMN_NAME_SCORE = "score";
    }
}
