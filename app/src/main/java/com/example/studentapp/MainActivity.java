package com.example.studentapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etId;
    private CheckBox cbEnrolled;
    private ListView listView;

    private DatabaseHelper db;
    private ArrayAdapter<DatabaseHelper.Student> adapter;
    private final List<DatabaseHelper.Student> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);
        cbEnrolled = findViewById(R.id.cbEnrolled);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, students);
        listView.setAdapter(adapter);

        // Tap a row to load it into the form (then press EDIT or DELETE)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            DatabaseHelper.Student s = students.get(position);
            etName.setText(s.name);
            etId.setText(s.id);
            cbEnrolled.setChecked(s.enrolled);
        });

        ((Button) findViewById(R.id.btnAdd)).setOnClickListener(v -> addStudent());
        ((Button) findViewById(R.id.btnEdit)).setOnClickListener(v -> editStudent());
        ((Button) findViewById(R.id.btnDelete)).setOnClickListener(v -> deleteStudent());
        ((Button) findViewById(R.id.btnView)).setOnClickListener(v -> refreshList());

        refreshList();
    }

    private boolean validate() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name is required");
            return false;
        }
        if (etId.getText().toString().trim().isEmpty()) {
            etId.setError("Student ID is required");
            return false;
        }
        return true;
    }

    private DatabaseHelper.Student readForm() {
        return new DatabaseHelper.Student(
                etId.getText().toString().trim(),
                etName.getText().toString().trim(),
                cbEnrolled.isChecked());
    }

    private void addStudent() {
        if (!validate()) return;
        if (db.addStudent(readForm())) {
            toast("Student added");
            clearForm();
            refreshList();
        } else {
            toast("Student ID already exists");
        }
    }

    private void editStudent() {
        if (!validate()) return;
        if (db.updateStudent(readForm())) {
            toast("Student updated");
            clearForm();
            refreshList();
        } else {
            toast("No student with that ID");
        }
    }

    private void deleteStudent() {
        String id = etId.getText().toString().trim();
        if (id.isEmpty()) {
            etId.setError("Enter or select a Student ID");
            return;
        }
        if (db.deleteStudent(id)) {
            toast("Student deleted");
            clearForm();
            refreshList();
        } else {
            toast("No student with that ID");
        }
    }

    private void refreshList() {
        students.clear();
        students.addAll(db.getAllStudents());
        adapter.notifyDataSetChanged();
    }

    private void clearForm() {
        etName.setText("");
        etId.setText("");
        cbEnrolled.setChecked(false);
        etName.requestFocus();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
