package com.pru_ybs.nilsusaffak.a20210305018_to_dolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else {
                // Veritabanına kaydet
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_FIRST_NAME, firstName);
                values.put(DatabaseHelper.COLUMN_LAST_NAME, lastName);
                values.put(DatabaseHelper.COLUMN_PASSWORD, password);

                long id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
                if (id > 0) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, TaskListActivity.class);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("lastName", lastName);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
        });
    }


}

