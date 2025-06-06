package com.example.mindflex;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mindflex.database.DailyActivityManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DailyScreenActivity extends AppCompatActivity {
    List<float[]> rawData = new ArrayList<>();
    private BarChart barChart;

    private Toast currentToast;
    private LocalDate startOfTheWeek = LocalDate.now().with(DayOfWeek.MONDAY);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String[] game_ids = {"Chimp Game", "Verbal Game", "Number Game",
            "Reaction Game", "Sequence Game", "Typing Game"};

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_screen);

        View rootView = findViewById(android.R.id.content);

        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        barChart = findViewById(R.id.weekly_chart);
        loadChartData();

        ImageView backButton = findViewById(R.id.daily_activity_back_button);
        backButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            onBackPressed();
        });

        ImageView dateLeft = findViewById(R.id.daily_activity_left_button);
        ImageView dateRight = findViewById(R.id.daily_activity_right_button);
        TextView dateRange = findViewById(R.id.daily_activity_date);

        dateLeft.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            startOfTheWeek = startOfTheWeek.minusWeeks(1);
            loadChartData();
            updateWeek();
        });

        dateRight.setOnClickListener(v->{
            HapticFeedbackManager.HapticFeedbackLight(v);
            startOfTheWeek = startOfTheWeek.plusWeeks(1);
            loadChartData();
            updateWeek();
        });

        dateRange.setOnClickListener(v-> datePicker());

        updateWeek();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadChartData() {
        executorService.execute(() -> DailyActivityManager.getAllGamesActivities(this, activities -> {
            Map<LocalDate, float[]> localDateMap = new HashMap<>();

            for (com.example.mindflex.database.DailyActivity dailyActivity : activities) {
                LocalDate date = LocalDate.parse(dailyActivity.date);
                float[] gameCount = localDateMap.getOrDefault(date, new float[6]);

                int index = getGameIndex(dailyActivity.gameID);
                if (index != -1) {
                    assert gameCount != null;
                    gameCount[index] += dailyActivity.timesPlayed;
                    localDateMap.put(date, gameCount);
                }
            }

            Log.d("ChartData", "Total activities received: " + activities.size());
            for (com.example.mindflex.database.DailyActivity a : activities) {
                Log.d("ChartData", "Activity: " + a.gameID + " | " + a.date + " | Times: " + a.timesPlayed);
            }

            List<BarEntry> entries = new ArrayList<>();
            List<float[]> finalData = new ArrayList<>();
            LocalDate today = LocalDate.now();

            LocalDate startDate = startOfTheWeek;
            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);
                float[] values = localDateMap.getOrDefault(date, new float[6]);
                entries.add(new BarEntry(i, values));
                finalData.add(values);
            }


            runOnUiThread(() -> updateChart(entries, finalData));
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateChart(List<BarEntry> barEntries, List<float[]> inputData) {
        this.rawData = inputData;

        BarDataSet barDataSet = new BarDataSet(barEntries, "Games / day");

        Log.d("DebugData", "barDataSet" + barDataSet);

        barDataSet.setColors(
                ContextCompat.getColor(this, R.color.chimp_main),
                ContextCompat.getColor(this, R.color.green),
                ContextCompat.getColor(this, R.color.number_main),
                ContextCompat.getColor(this, R.color.reaction_main),
                ContextCompat.getColor(this, R.color.sequence_main),
                ContextCompat.getColor(this, R.color.type_main)
        );

        barDataSet.setStackLabels(new String[]{
                "Chimp Game", "Letter Game", "Number Game",
                "Reaction Game", "Sequence Game", "Typing Game"
        });

        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.8f);
        barChart.setData(barData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setHighlightPerDragEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            final List<String> dayLabels = getShiftedWeekLabels();

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dayLabels.size()) {
                    return dayLabels.get(index);
                } else {
                    return "";
                }
            }
        });

        YAxis yAxis = barChart.getAxisLeft();
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxis.setAxisMinimum(0f);

        Legend legend = barChart.getLegend();
        Description description = barChart.getDescription();
        legend.setEnabled(false);
        description.setEnabled(false);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int dayIndex = (int) e.getX();
                int gameIndex = h.getStackIndex();
                String[] gameName = {
                        "Chimp Game", "Letter Game", "Number Game",
                        "Reaction Game", "Sequence Game", "Typing Game"
                };

                if (dayIndex >= 0 && dayIndex < rawData.size() && gameIndex >= 0 && gameIndex < gameName.length) {
                    float value = rawData.get(dayIndex)[gameIndex];
                    String gameNameFinal = gameName[gameIndex];
                    String finalMsg = gameNameFinal + " | Times played: " + (int) value;

                    if (value != 0) {
                        if (currentToast != null) {
                            currentToast.cancel();
                        }
                        currentToast = Toast.makeText(DailyScreenActivity.this, finalMsg, Toast.LENGTH_SHORT);
                        currentToast.show();
                    }
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart.invalidate();
    }

    List<String> getShiftedWeekLabels() {
        return Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
    }

    private int getGameIndex(String gameID) {
        for (int i = 0; i < game_ids.length; i++) {
            if (game_ids[i].equals(gameID)) {
                return i;
            }
        }
        return -1;
    }

    private void updateWeek() {
        LocalDate endOfWeek = startOfTheWeek.plusDays(6);
        String dateText = startOfTheWeek.getDayOfMonth() + " " + startOfTheWeek.getMonth().name().substring(0, 3) +
                " - " + endOfWeek.getDayOfMonth() + " " + endOfWeek.getMonth().name().substring(0, 3);

        TextView textViewDate = findViewById(R.id.daily_activity_date);
        textViewDate.setText(dateText);
    }

    private void datePicker() {
        LocalDate currentDate = LocalDate.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerTheme,
                (view, year, month, dayOfMonth) -> {
                    LocalDate selected = LocalDate.of(year, month + 1, dayOfMonth);
                    startOfTheWeek = selected.with(DayOfWeek.MONDAY);
                    loadChartData();
                    updateWeek();
                },
                currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());
                datePickerDialog.show();
    }
}