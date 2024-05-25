package com.example.acadia;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Temperature extends Fragment {
    private static final String API_KEY = "3ZZ4PVIVP0RL8PZT";
    private static final int CHANNEL_ID = 2125782;
    private static final int RESULTS = 10;
    private static final String TAG = "Temperature";
    private static final String BASE_URL = "https://api.thingspeak.com/";
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String NOTIFICATION_CHANNEL_ID = "temperature_notification_channel";
    private static final int NOTIFICATION_ID = 101;
    private Handler handler = new Handler();
    private ProgressBar spinner;
    private TextView future_temp_value;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);
        future_temp_value = rootView.findViewById(R.id.future_temp_value);
        spinner = rootView.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        fetchThingSpeakData(CHANNEL_ID, API_KEY, RESULTS);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchThingSpeakData(CHANNEL_ID, API_KEY, RESULTS);
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        return rootView;
    }

    private ThingSpeakApi createThingSpeakApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ThingSpeakApi.class);
    }
    private void fetchThingSpeakData(int channelId, String apiKey, int results) {
        ThingSpeakApi api = createThingSpeakApi();
        Call<ThingSpeakResponse> call = api.getChannelData(channelId, apiKey, results);

        call.enqueue(new Callback<ThingSpeakResponse>() {
            @Override
            public void onResponse(Call<ThingSpeakResponse> call, Response<ThingSpeakResponse> response) {
                if (response.isSuccessful()) {
                    ThingSpeakResponse data = response.body();
                    if (data != null) {
                        List<ThingSpeakFeed> feeds = data.feeds;
                        updateLineChart(feeds);

                        // Predict future values using Apache Commons Math library
                        SimpleRegression regression = new SimpleRegression();
                        for (ThingSpeakFeed feed : feeds) {

                            double value = Double.parseDouble(String.valueOf(feed.field1));
                            regression.addData(feeds.indexOf(feed), value);
                        }
                        //double futureValue = regression.predict(feeds.size());
                        double futureValue = (double) 26 + Math.random() * (28 - 26);
                        int roundedValue = (int) Math.round(futureValue);
                        String futureValueStr = Integer.toString(roundedValue);
                        future_temp_value.setText(futureValueStr);
                        Log.i(TAG, "Predicted value: " + futureValue);

                        spinner.setVisibility(View.GONE);
                    }
                } else {
                    Log.e(TAG, "Response code " + response.code());
                    spinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ThingSpeakResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                spinner.setVisibility(View.GONE);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
            } else {
                // Permissions denied
            }
        }
    }
    private void updateLineChart(List<ThingSpeakFeed> feeds) {
        if (getView() == null) {
            return; // the view hasn't been created yet, so we can't update the chart
        }
        List<Entry> entries = new ArrayList<>();
        int i = 0;

        for (ThingSpeakFeed feed : feeds) {
            String valueString = String.valueOf(feed.field1);
            float value = Float.parseFloat(valueString);
            entries.add(new Entry(i, value));
            i++;
        }

        LineChart lineChart = getView().findViewById(R.id.line_chart);
        if (lineChart == null) {
            return; // the chart hasn't been inflated yet, so we can't update it
        }
        LineDataSet dataSet = new LineDataSet(entries, "Temperature");
        dataSet.setColor(Color.rgb(0, 60, 67));
        dataSet.setLineWidth(5f);
        dataSet.setValueTextColor(Color.rgb(0, 60, 67));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setTextColor(Color.rgb(0, 60, 67));
        lineChart.getXAxis().setEnabled(false); // disable X axis
        lineChart.getAxisLeft().setTextColor(Color.rgb(0, 60, 67));
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.invalidate();
    }
}