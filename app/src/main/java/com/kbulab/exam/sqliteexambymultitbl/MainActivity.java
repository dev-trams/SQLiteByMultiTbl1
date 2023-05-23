package com.kbulab.exam.sqliteexambymultitbl;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btn1, btn2, btn3, btn4;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB db = new DB(this, "hw", null, 1);
        SQLiteDatabase database = db.getWritableDatabase();
        db.onUpgrade(database, 1, 2);
        insertPeople(db);
        insertPosition(db);


        textView = (TextView) findViewById(R.id.textView);
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoad("position", db);
            }
        });
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoad("people", db);
            }
        });
        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchList(db, "이상");
            }
        });
        btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchList(db, "미만");
            }
        });
    }
    private void onSearchList(DB db, String state) {
        Cursor cursor = db.onSearchData("급여", 12000, state);
        textView.setText("");
        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity.this, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }else {
            textView.setText("---- INNER JOIN with ".concat(state.equals("이상")?"rawQuery":"query").concat("----"));
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String c1 = cursor.getString(0);
                String c2 = cursor.getString(1);
                int c3 = cursor.getInt(2);

                textView.append("\n name = ".concat(c1)
                        .concat("position = ").concat(c2)
                        .concat("salary = ").concat(String.valueOf(c3)));

                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    private void onLoad(String mode, DB db) {
        textView.setText("");
        Cursor cursor = mode.equals("people") ? db.searchData("people") :
                mode.equals("position") ? db.searchData("position") : null;

        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity.this, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToFirst();
            textView.setText("---- ".concat(mode.equals("position")?"Position":"People").concat(" table").concat(" ----\n"));
            for (int i = 0; i < cursor.getCount(); i++) {
                int c1 = cursor.getInt(0);
                String c2 = cursor.getString(1);
                int c3 = cursor.getInt(2);

                textView.append("id = " + c1 + (mode.equals("people") ? " name = " : mode.equals("position") ? " position = " : "") + c2 + (mode.equals("people") ? " posid = " : mode.equals("position") ? "salary" : "") + c3 + "\n");

                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    private void insertPeople(DB db) {
        db.insertData("people", null, "홍길동", 2);
        db.insertData("people", null, "이대한", 3);
        db.insertData("people", null, "한민국", 2);
        db.insertData("people", null, "이순신", 2);
        db.insertData("people", null, "정약용", 3);
        db.insertData("people", null, "코로나", 1);
        db.insertData("people", null, "장건우", 2);
        db.insertData("people", null, "박과장", 4);
    }

    private void insertPosition(DB db) {
        db.insertData("position", "1", "부장", 15000);
        db.insertData("position", "2", "과장", 12000);
        db.insertData("position", "3", "대리", 10000);
        db.insertData("position", "4", "사원", 8000);
    }
}