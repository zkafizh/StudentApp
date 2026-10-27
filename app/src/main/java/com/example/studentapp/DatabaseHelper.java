package com.example.studentapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "students.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "students";

    public static class Student {
        public final String id;
        public final String name;
        public final boolean enrolled;

        public Student(String id, String name, boolean enrolled) {
            this.id = id;
            this.name = name;
            this.enrolled = enrolled;
        }

        @Override
        public String toString() {
            return name + "\nID: " + id + "  |  " + (enrolled ? "Enrolled" : "Not enrolled");
        }
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "student_id TEXT PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "enrolled INTEGER NOT NULL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /** Returns true if inserted, false if the ID already exists. */
    public boolean addStudent(Student s) {
        ContentValues cv = new ContentValues();
        cv.put("student_id", s.id);
        cv.put("name", s.name);
        cv.put("enrolled", s.enrolled ? 1 : 0);
        return getWritableDatabase().insert(TABLE, null, cv) != -1;
    }

    /** Updates name + enrolled for the given student ID. */
    public boolean updateStudent(Student s) {
        ContentValues cv = new ContentValues();
        cv.put("name", s.name);
        cv.put("enrolled", s.enrolled ? 1 : 0);
        return getWritableDatabase().update(TABLE, cv, "student_id = ?", new String[]{s.id}) > 0;
    }

    public boolean deleteStudent(String id) {
        return getWritableDatabase().delete(TABLE, "student_id = ?", new String[]{id}) > 0;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT student_id, name, enrolled FROM " + TABLE + " ORDER BY name COLLATE NOCASE", null)) {
            while (c.moveToNext()) {
                list.add(new Student(c.getString(0), c.getString(1), c.getInt(2) == 1));
            }
        }
        return list;
    }
}
