package edu.hitsz.rank;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteHelper";
    private static final String DB_NAME = "rank.db";

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS record (record_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, score INTEGER, date Date, gameType INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS record");
        onCreate(db);
    }

    public LinkedList<Record> getRecords(int gameType) {
        LinkedList<Record> records = new LinkedList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM record WHERE gameType = " + gameType + " ORDER BY score DESC";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id_idx = cursor.getColumnIndex("record_id");
            int name_idx = cursor.getColumnIndex("name");
            int score_idx = cursor.getColumnIndex("score");
            int date_idx = cursor.getColumnIndex("date");
            assert id_idx != -1 && name_idx != -1 && score_idx != -1 && date_idx != -1;
            int record_id = cursor.getInt(id_idx);
            String name = cursor.getString(name_idx);
            int score = cursor.getInt(score_idx);
            Date date = new Date(cursor.getLong(date_idx));
            records.add(new Record(name, score, record_id, date));
        }
        cursor.close();
        return records;
    }

    public void insertRecord(Record record, int gameType) {
        SQLiteDatabase db = getWritableDatabase();
        if(record.getRecord_id() == -1) {
            int new_id = getSuitableId(db);
            record.setRecord_id(new_id);
        }
        String sql = "INSERT INTO record (name, score, date, gameType) VALUES (?, ?, ?, ?)";
        db.execSQL(sql, new Object[]{record.getName(), record.getScore(), record.getDate().getTime(), gameType});
    }

    public int getSuitableId(SQLiteDatabase db) {
        String sql = "SELECT MAX(record_id) FROM record";
        Cursor cursor = db.rawQuery(sql, null);
        int id = 0;
        if(cursor.moveToNext()) {
            id = cursor.getInt(0) + 1;
        }
        cursor.close();
        return id;
    }

    public void deleteRecord(int record_id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM record WHERE record_id = ?";
        db.execSQL(sql, new Object[]{record_id});
    }
}
