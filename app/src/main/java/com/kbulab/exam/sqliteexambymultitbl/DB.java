package com.kbulab.exam.sqliteexambymultitbl;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private Context context;
    String TBLName1 = "people";
    String TBLName2 = "position";

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE IF NOT EXISTS " + TBLName1 + "(id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Name TEXT, Positionid INTEGER);";
        db.execSQL(sql1);
        String sql2 = "CREATE TABLE IF NOT EXISTS " + TBLName2 + "(id INTEGER, " + " position TEXT, salary INTEGER, FOREIGN KEY (id) REFERENCES " + TBLName1 + "(Positionid));";
        db.execSQL(sql2);
    }

    public long insertData(String mode, String d1, String d2, int d3) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues contentValues = null;
            switch (mode) {
                case "people":
                    contentValues = new ContentValues();
                    contentValues.put("Name", d2);
                    contentValues.put("Positionid", d3);
                    break;
                case "position":
                    contentValues = new ContentValues();
                    contentValues.put("id", Integer.valueOf(d1));
                    contentValues.put("position", d2);
                    contentValues.put("salary", d3);
                    break;

            }
            long insertedRowId = database.insertWithOnConflict(mode.equals("people") ? TBLName1 : mode.equals("position")?TBLName2:"NONE", null, contentValues, SQLiteDatabase.CONFLICT_NONE);
            database.setTransactionSuccessful();
            return insertedRowId;
        } catch (Exception e) {
            return -1L;
        } finally {
            database.endTransaction();
        }

    }

    public Cursor searchData(String tableName) {
        SQLiteDatabase database = getWritableDatabase();
        String TBL = tableName.equals("people") ? TBLName1 : tableName.equals("position") ? TBLName2 : "none";
        String sql = "SELECT * FROM " + TBL + ";";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor;
    }

    public Cursor onSearchData(String condition1, int condition2, String condition3) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "SELECT * FROM "+TBLName1+";";
        Cursor cursor = null;
        switch (condition1){
            case "급여":
                switch (condition2) {
                    case 12000:
                        switch (condition3) {
                            case "이상":
                                 sql = "SELECT "+TBLName1+ ".Name,"+TBLName2+".position,"+TBLName2+".salary FROM "+ TBLName1 +
                                        " INNER JOIN "+TBLName2+" ON "+TBLName1+".Positionid = "+TBLName2+".id" +
                                        " WHERE "+TBLName2+".salary >= 12000;";
                                cursor = database.rawQuery(sql, null);
                                break;
                            case "미만":
                                String[] projection = {TBLName1 + ".Name", TBLName2 + ".position", TBLName2 + ".salary"};
                                String selection = TBLName2 + ".salary < ?";
                                String[] selectionArgs = {"12000"};
                                String joinClause = TBLName1 + " INNER JOIN " + TBLName2 + " ON " + TBLName1 + ".Positionid = " + TBLName2 + ".id";
                                cursor = database.query(joinClause, projection, selection, selectionArgs, null, null, null);
                                break;
                        }
                        break;
                }
                break;
        }

        return cursor;
    }


    public void deleteData(int index) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            String sql = "DELETE FROM " + TBLName1 + " WHERE CODE = ?";
            String[] whereArgs = new String[]{"CD-010" + index};
            database.execSQL(sql, whereArgs);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }

    }

    public void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            String sql = "DELETE FROM " + TBLName1;
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    @SuppressLint("Range")
    public void onUpdate(float price) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        SQLiteStatement statement = null;
        try {
            String sql = "INSERT OR REPLACE INTO " + TBLName1 + " (CODE, name, price) VALUES (?, ?, ?)";
            statement = database.compileStatement(sql);

            String selectSql = "SELECT CODE, name, price FROM " + TBLName1;
            Cursor cursor = database.rawQuery(selectSql, null);

            while (cursor.moveToNext()) {
                String code = cursor.getString(cursor.getColumnIndex("CODE"));
                String name = cursor.getString(cursor.getColumnIndex("name"));

                statement.bindString(1, code);
                statement.bindString(2, name);
                statement.bindDouble(3, Double.parseDouble(String.valueOf(price)));
                statement.execute();
                statement.clearBindings();
            }

            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
            database.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql1 = "DROP TABLE IF EXISTS " + TBLName1 + ";";
        String sql2 = "DROP TABLE IF EXISTS " + TBLName2 + ";";
        db.execSQL(sql1);
        db.execSQL(sql2);
        onCreate(db);
    }
}
