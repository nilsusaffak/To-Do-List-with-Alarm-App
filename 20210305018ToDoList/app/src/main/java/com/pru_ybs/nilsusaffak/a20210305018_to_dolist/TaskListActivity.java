package com.pru_ybs.nilsusaffak.a20210305018_to_dolist;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.BreakIterator;

public class TaskListActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private LinearLayout tasksContainer; // Görevlerin gösterileceği alan
    private static final int ADD_TASK_REQUEST_CODE = 1;
    private DatabaseHelper dbHelper;  // DatabaseHelper nesnesi



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);


        //Menü
        // Toolbar'ı ayarla
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // Android 13 ve sonrası için bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        // Hoşgeldiniz Mesajını Ayarla
        welcomeTextView = findViewById(R.id.welcomeTextView);
        Intent intent = getIntent();
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        welcomeTextView.setText("WELCOME!, " + firstName + " " + lastName);

        // Görevlerin gösterileceği LinearLayout
        tasksContainer = findViewById(R.id.tasksContainer);

        // DatabaseHelper örneği oluştur
        dbHelper = new DatabaseHelper(this);

        // Veritabanından görevleri yükle
        loadTasksFromDatabase();

        // Yeni Görev Ekleme Butonu
        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(view -> {
            Intent addTaskIntent = new Intent(TaskListActivity.this, AddNewTaskActivity.class);
            startActivityForResult(addTaskIntent, ADD_TASK_REQUEST_CODE);
        });
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu); // menu.xml dosyasının yolu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.loginscreen) {
            // Login Screen'e geçiş yap
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.newtask) {
            // New Task sayfasına geçiş yap
            Intent intent = new Intent(this, AddNewTaskActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    // Yeni görev ekleme işlemi
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String taskName = data.getStringExtra("taskName");
            String startDate = data.getStringExtra("startDate");
            String startTime = data.getStringExtra("startTime");
            String endDate = data.getStringExtra("endDate");
            String endTime = data.getStringExtra("endTime");

            // Yeni görevi veritabanına kaydet
            saveTaskToDatabase(taskName, startDate, startTime, endDate, endTime);

            // Yeni görevi ekle
            addTaskToLayout(taskName, startDate, startTime, endDate, endTime);
        }
    }

    // LinearLayout içine yeni görev ekleme
    private void addTaskToLayout(String taskName, String startDate, String startTime, String endDate, String endTime) {
        // Yeni bir görev görünümü oluştur
        View taskView = LayoutInflater.from(this).inflate(R.layout.item_task, tasksContainer, false);

        TextView taskNameTextView = taskView.findViewById(R.id.taskNameTextView);
        TextView taskDetailsTextView = taskView.findViewById(R.id.taskDetailsTextView);
        CheckBox taskCheckBox = taskView.findViewById(R.id.taskCheckBox);
        Button deleteTaskButton = taskView.findViewById(R.id.deleteTaskButton);  // Sil butonu

        // Görev bilgilerini ayarla
        taskNameTextView.setText(taskName);
        taskDetailsTextView.setText("Start: " + startDate + " " + startTime + "\nEnd: " + endDate + " " + endTime);

        // Silme butonunun tıklanma olayı
        deleteTaskButton.setOnClickListener(v -> {
            // Görevi sil
            deleteTaskFromDatabase(taskName);
            tasksContainer.removeView(taskView);  // UI'dan sil
            Toast.makeText(TaskListActivity.this, taskName + " deleted!", Toast.LENGTH_SHORT).show();
        });

        // CheckBox tıklama olayları
        taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(TaskListActivity.this, taskName + " Completed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TaskListActivity.this, taskName + " Cancelled!", Toast.LENGTH_SHORT).show();
            }
        });

        // Görevi listeye ekle
        tasksContainer.addView(taskView);
    }

    // Veritabanından görevleri yükle
    private void loadTasksFromDatabase() {
        // Veritabanından görevleri yükleyip ekleyelim
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TASKS, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            // Sütun index'ini kontrol et
            int taskNameColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_NAME);
            int startDateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_DATE);
            int startTimeColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME);
            int endDateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_DATE);
            int endTimeColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME);

            // Eğer sütunlar doğru şekilde bulunamazsa hata mesajı göster
            if (taskNameColumnIndex == -1 || startDateColumnIndex == -1 || startTimeColumnIndex == -1 ||
                    endDateColumnIndex == -1 || endTimeColumnIndex == -1) {
                Toast.makeText(this, "Data columns are missing or incorrect!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verileri al
            String taskName = cursor.getString(taskNameColumnIndex);
            String startDate = cursor.getString(startDateColumnIndex);
            String startTime = cursor.getString(startTimeColumnIndex);
            String endDate = cursor.getString(endDateColumnIndex);
            String endTime = cursor.getString(endTimeColumnIndex);

            // Görevi ekle
            addTaskToLayout(taskName, startDate, startTime, endDate, endTime);
        }
        cursor.close();
        db.close();
    }

    // Yeni görevi veritabanına kaydet
    private void saveTaskToDatabase(String taskName, String startDate, String startTime, String endDate, String endTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_NAME, taskName);
        values.put(DatabaseHelper.COLUMN_START_DATE, startDate);
        values.put(DatabaseHelper.COLUMN_START_TIME, startTime);
        values.put(DatabaseHelper.COLUMN_END_DATE, endDate);
        values.put(DatabaseHelper.COLUMN_END_TIME, endTime);

        // Veriyi kaydet
        long id = db.insert(DatabaseHelper.TABLE_TASKS, null, values);
        db.close();

        if (id > 0) {
            Toast.makeText(this, "Task Added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unable to Add Task!", Toast.LENGTH_SHORT).show();
        }
    }

    // Veritabanından görevi sil
    private void deleteTaskFromDatabase(String taskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.COLUMN_TASK_NAME + " = ?";
        String[] selectionArgs = {taskName};
        db.delete(DatabaseHelper.TABLE_TASKS, selection, selectionArgs);
        db.close();
    }


}











