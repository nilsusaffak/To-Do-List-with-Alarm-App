package com.pru_ybs.nilsusaffak.a20210305018_to_dolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Veritabanı Bilgileri
    private static final String DATABASE_NAME = "LifeOrganizer.db";
    private static final int DATABASE_VERSION = 1;

    // Kullanıcı Tablosu
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PASSWORD = "password";

    // Görevler Tablosu
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_TASK_ID = "task_id";
    public static final String COLUMN_TASK_NAME = "task_name";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_END_TIME = "end_time";

    // Kullanıcı Tablosu Oluşturma Sorgusu
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FIRST_NAME + " TEXT, " +
            COLUMN_LAST_NAME + " TEXT, " +
            COLUMN_PASSWORD + " TEXT)";

    // Görevler Tablosu Oluşturma Sorgusu
    private static final String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
            COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TASK_NAME + " TEXT, " +
            COLUMN_START_DATE + " TEXT, " +
            COLUMN_START_TIME + " TEXT, " +
            COLUMN_END_DATE + " TEXT, " +
            COLUMN_END_TIME + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }
}

