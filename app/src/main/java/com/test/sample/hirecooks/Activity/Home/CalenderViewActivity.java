package com.test.sample.hirecooks.Activity.Home;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.test.sample.hirecooks.R;
import java.util.Calendar;

public class CalenderViewActivity extends AppCompatActivity {
    Calendar calendar;
    CalendarView calendarView;
    TextView txtDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_view);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 9);
        calendar.set(Calendar.YEAR, 2012);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, 1);
        calendarView = findViewById(R.id.calendarView);
        txtDate = findViewById(R.id.date);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String msg = "Selected date Day: " + day + " Month : " + (month + 1) + " Year " + year;
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                txtDate.setText(day + "-" + (month + 1) + "-" + year);
            }
        });
    }
}

