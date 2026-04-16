package com.example.projektaplikacjemobilne;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BazaDanychZadania extends SQLiteOpenHelper {
    public BazaDanychZadania (Context context) {
        super(context, "dataBaseTasks.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE uzytkownicy (id INTEGER PRIMARY KEY AUTOINCREMENT, imie TEXT, login TEXT UNIQUE, haslo TEXT)");
        db.execSQL("CREATE TABLE zadania (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, nazwa TEXT, opis TEXT, data_dodania TEXT, deadline TEXT, priorytet INTEGER, kategoria TEXT, status INTEGER)");

        db.execSQL("INSERT INTO uzytkownicy(imie, login, haslo) VALUES('Wiktor','u1','1234')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS zadania");
        db.execSQL("DROP TABLE IF EXISTS uzytkownicy");

        onCreate(db);
    }
}