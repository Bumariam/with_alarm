package com.example.bodyboost;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class WaterActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "WaterTrackerPrefs";
    private static final String KEY_CONSUMED_WATER = "consumedWater";
    private static final String KEY_LAST_DATE = "lastDate";

    private int dailyGoal = 2000; // Дневная норма воды
    private int consumedWater = 0; // Потребленная вода

    private TextView tvDailyGoal;
    private TextView tvConsumedWater;
    private Button btnAdd50ml;
    private Button btnAdd100ml;
    private Button btnAdd150ml;
    private Button btnAdd200ml;
    private Button btnDeleteEntry;
    private ProgressBar progressBar;
    private TextView tvPercentage;
    private CardView reminderCard;
    private RadioGroup radioGroup;
    private Button btnSetReminder;

    private Button button2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        tvDailyGoal = findViewById(R.id.tvDailyGoal);
        tvConsumedWater = findViewById(R.id.tvConsumedWater);
        btnAdd50ml = findViewById(R.id.btnAdd50ml);
        btnAdd100ml = findViewById(R.id.btnAdd100ml);
        btnAdd150ml = findViewById(R.id.btnAdd150ml);
        btnAdd200ml = findViewById(R.id.btnAdd200ml);
        btnDeleteEntry = findViewById(R.id.btnDeleteEntry);
        progressBar = findViewById(R.id.progressBar);
        tvPercentage = findViewById(R.id.tvPercentage);
        reminderCard = findViewById(R.id.reminderCard);
        radioGroup = findViewById(R.id.radioGroup);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        button2 = findViewById(R.id.button2);

        loadConsumedWater(); // Загрузка сохраненного значения потребленной воды

        updateUI();

        btnAdd50ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(50);
            }
        });

        btnAdd100ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(100);
            }
        });

        btnAdd150ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(150);
            }
        });

        btnAdd200ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(200);
            }
        });

        btnDeleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });

        btnSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReminderCard();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WaterActivity.this, TestActivity.class));
                finish();
            }
        });



    }

    private void loadConsumedWater() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        consumedWater = preferences.getInt(KEY_CONSUMED_WATER, 0);

        long lastSavedTime = preferences.getLong(KEY_LAST_DATE, 0);
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTimeInMillis(lastSavedTime);

        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.DAY_OF_YEAR, -1); // Отнимаем 1 день от текущей даты

        if (lastDate.before(currentTime)) {
            // Если прошло более 24 часов с последнего сохранения, сбрасываем значения
            consumedWater = 0;
            saveConsumedWater();
        }
    }

    private void saveConsumedWater() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_CONSUMED_WATER, consumedWater);
        editor.putLong(KEY_LAST_DATE, Calendar.getInstance().getTimeInMillis());
        editor.apply();
    }

    private void addWater(int amount) {
        consumedWater += amount; // Увеличиваем потребление на указанное количество
        saveConsumedWater();
        updateUI();
    }

    private void deleteEntry() {
        consumedWater = 0; // Устанавливаем потребленную воду в 0
        saveConsumedWater();
        updateUI();
    }

    private void updateUI() {
        tvDailyGoal.setText("Дневная норма: " + dailyGoal + " мл");
        tvConsumedWater.setText("Потреблено: " + consumedWater + " мл");
        progressBar.setProgress(consumedWater);
        progressBar.setSecondaryProgress(consumedWater); // Устанавливаем вторичное значение для заполненности
        updateProgressBarPercentage();
    }

    private void updateProgressBarPercentage() {
        int progress = (int) ((float) consumedWater / (float) dailyGoal * 100); // Расчет процента заполненности
        tvPercentage.setText(progress + "%");
    }

    public class WaterReminderBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Пора пить воду!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showReminderCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View reminderCardView = LayoutInflater.from(this).inflate(R.layout.reminder_card, null);
        builder.setView(reminderCardView);
        AlertDialog dialog = builder.create();

        // Настройка обработчика нажатия кнопки "Установить напоминание"
        Button btnSetReminder = reminderCardView.findViewById(R.id.btnSetReminder);
        RadioGroup radioGroup = reminderCardView.findViewById(R.id.radioGroup);
        btnSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedInterval = 0;
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.radioButton30min) {
                    selectedInterval = 30;
                } else if (checkedRadioButtonId == R.id.radioButton1hour) {
                    selectedInterval = 60;
                } else if (checkedRadioButtonId == R.id.radioButton2hours) {
                    selectedInterval = 120;
                }
                setReminder(selectedInterval);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setReminder(int interval) {
        Intent intent = new Intent(this, WaterReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Установка напоминания с выбранным интервалом
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval * 60 * 1000, pendingIntent);

        // Отображение сообщения о том, что интервал установлен
        String message = "Интервал напоминания установлен: " + interval + " минут";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }
}
