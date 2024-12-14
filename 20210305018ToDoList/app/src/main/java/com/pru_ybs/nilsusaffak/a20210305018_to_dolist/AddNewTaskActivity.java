package com.pru_ybs.nilsusaffak.a20210305018_to_dolist;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class AddNewTaskActivity extends AppCompatActivity {

    private EditText taskNameEditText, startDateEditText, startTimeEditText, endDateEditText, endTimeEditText;
    private Button saveButton;
    private DatabaseHelper dbHelper;  // DatabaseHelper nesnesi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        // Veritabanı yardımcı sınıfını başlat
        dbHelper = new DatabaseHelper(this);

        // EditText ve Button bileşenlerini bağlama
        taskNameEditText = findViewById(R.id.taskNameEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        saveButton = findViewById(R.id.saveButton);

        // Bildirim izni kontrolü (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Kaydet butonuna tıklama işlemi
        saveButton.setOnClickListener(view -> {
            String taskName = taskNameEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String startTime = startTimeEditText.getText().toString();
            String endDate = endDateEditText.getText().toString();
            String endTime = endTimeEditText.getText().toString();

            if (taskName.isEmpty() || startDate.isEmpty() || startTime.isEmpty() || endDate.isEmpty() || endTime.isEmpty()) {
                // Boş alan kontrolü
                Toast.makeText(AddNewTaskActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else {
                // Veritabanına görev kaydetme
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_TASK_NAME, taskName);
                values.put(DatabaseHelper.COLUMN_START_DATE, startDate);
                values.put(DatabaseHelper.COLUMN_START_TIME, startTime);
                values.put(DatabaseHelper.COLUMN_END_DATE, endDate);
                values.put(DatabaseHelper.COLUMN_END_TIME, endTime);

                long id = db.insert(DatabaseHelper.TABLE_TASKS, null, values);
                db.close();

                if (id > 0) {
                    // Görev başarıyla eklendi
                    Toast.makeText(AddNewTaskActivity.this, "Task Added and Alarm Set!", Toast.LENGTH_SHORT).show();

                    // Alarm Kurma
                    setAlarm(taskName, startDate, startTime);

                    // Verileri geri gönder
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("taskName", taskName);
                    resultIntent.putExtra("startDate", startDate);
                    resultIntent.putExtra("startTime", startTime);
                    resultIntent.putExtra("endDate", endDate);
                    resultIntent.putExtra("endTime", endTime);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    // Görev eklenemedi
                    Toast.makeText(AddNewTaskActivity.this, "Unable to Add Task!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Alarm kurma işlemi
    private void setAlarm(String taskName, String startDate, String startTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("taskName", taskName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Alarm için tarih ve saat ayarı
        Calendar calendar = Calendar.getInstance();
        String[] dateParts = startDate.split("/");
        String[] timeParts = startTime.split(":");

        if (dateParts.length == 3 && timeParts.length == 2) {
            int year = Integer.parseInt(dateParts[2]);
            int month = Integer.parseInt(dateParts[1]) - 1;  // Aylar 0 tabanlı
            int day = Integer.parseInt(dateParts[0]);
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            calendar.set(year, month, day, hour, minute, 0);

            if (alarmManager != null) {
                // setExact yerine set() kullanıyoruz
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            Toast.makeText(this, "Date or Time Format Invalid!", Toast.LENGTH_SHORT).show();
        }
    }

}




