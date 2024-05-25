package com.example.acadia;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends Fragment {
    private static final String API_KEY = "3ZZ4PVIVP0RL8PZT";
    private static final int CHANNEL_ID = 2125782;
    private static final int RESULTS = 5;
    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://api.thingspeak.com/";
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar temp_progressBar;
    private ProgressBar air_progressBar;
    private ProgressBar co2_progressBar;
    private ProgressBar co_progressBar;
    private TextView device_temperature;
    private List<ThingSpeakFeed> feeds;
    private int bar_value;
    private int air_value;
    private int co2_value;
    private int co_value;

    String device_temp;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String NOTIFICATION_CHANNEL_ID = "temperature_notification_channel";
    private static final int NOTIFICATION_ID = 101;
    private static final int TEMPERATURE_THRESHOLD = 35;
    private Button air_btn;
    private Button co2_btn;
    private Button temp_btn;
    private TextView air_safe_level;
    private TextView co2_safe_level;
    private TextView temp_safe_level;
    private TextView greetingTextView;


    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        FragmentManager fragmentManager = getParentFragmentManager();
        air_safe_level = rootView.findViewById(R.id.air_current_level);
        co2_safe_level = rootView.findViewById(R.id.co2_current_level);
        temp_safe_level = rootView.findViewById(R.id.temp_current_level);
        greetingTextView = rootView.findViewById(R.id.greeting_view);
        air_btn = rootView.findViewById(R.id.air_btn);
        co2_btn = rootView.findViewById(R.id.co2_btn);
        temp_btn = rootView.findViewById(R.id.temp_btn);
        device_temperature = rootView.findViewById(R.id.device_temp);
        temp_progressBar = rootView.findViewById(R.id.temp_bar);
        air_progressBar = rootView.findViewById(R.id.airpoll_bar);
        co2_progressBar = rootView.findViewById(R.id.co2_bar);
        co_progressBar = rootView.findViewById(R.id.co_bar2);

        temp_progressBar.setProgress(0);
        temp_progressBar.setMax(50);
        air_progressBar.setProgress(0);
        air_progressBar.setMax(500);
        co2_progressBar.setProgress(0);
        co2_progressBar.setMax(2000);
        co_progressBar.setProgress(0);
        co_progressBar.setMax(50);

        // Get current hour of the day
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        // Set greeting based on time of day
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greetingTextView.setText("Good Morning");
        } else if (timeOfDay >= 12 && timeOfDay < 18) {
            greetingTextView.setText("Good Afternoon");
        } else if (timeOfDay >= 18 && timeOfDay < 24) {
            greetingTextView.setText("Good Evening");
        }

        fetchThingSpeakData(CHANNEL_ID, API_KEY, RESULTS);

        // Call fetchThingSpeakData() every 5 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchThingSpeakData(CHANNEL_ID, API_KEY, RESULTS);
                handler.postDelayed(this, 5000);
            }
        }, 1000);

        air_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new AirPollution());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        co2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new Carbon());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        temp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new Temperature());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
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

                        // Set progress bar value based on field1 of first feed
                        if (feeds.size() > 0) {
                            bar_value = (int) feeds.get(0).field1;
                            sendTemperatureNotification(getContext(), bar_value);

                            air_value = (int) feeds.get(0).field3;

                            co2_value = (int) feeds.get(0).field4;

                            co_value = (int) feeds.get(0).field5;

                            device_temp = String.valueOf(feeds.get(0).field7);



                            // Change progress bar tint if bar_value exceeds 32
                            if (bar_value > 35) {
                                temp_progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                                temp_safe_level.setText("Danger");
                            } else if ((bar_value > 30) || (bar_value <=35) ) {
                                temp_safe_level.setText("Moderate");
                            }
                            else{
                                temp_progressBar.setProgressTintList(null);
                                temp_safe_level.setText("Safe");
                            }
                            temp_progressBar.setProgress(bar_value);

                            // Change air progress bar tint if value exceeds 100
                            if (air_value > 100) {
                                air_progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                                air_safe_level.setText("Danger");
                            } else if ((air_value > 50) || (air_value <=100) ) {
                                air_progressBar.setProgressTintList(null);
                                air_safe_level.setText("Moderate");
                            }
                            else{
                                air_progressBar.setProgressTintList(null);
                                air_safe_level.setText("Safe");
                            }
                            air_progressBar.setProgress(air_value);
                            co2_progressBar.setProgress(co2_value);
                            co_progressBar.setProgress(co_value);
                            device_temperature.setText(device_temp);
                        }
                    }
                } else {
                    Log.e(TAG, "Response code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ThingSpeakResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    @Override
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


    private void sendTemperatureNotification(Context context, int temperature) {
        if (temperature > TEMPERATURE_THRESHOLD) {
            createNotificationChannel(context);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

            SpannableString contentTitle = new SpannableString("High Temperature Alert");
            contentTitle.setSpan(new ForegroundColorSpan(Color.rgb(0x21, 0x00, 0x62)), 0, contentTitle.length(), 0);

            SpannableString contentText = new SpannableString("Temperature is " + temperature + "°C. Take necessary precautions.");
            contentText.setSpan(new ForegroundColorSpan(Color.rgb(0x21, 0x00, 0x62)), 0, contentText.length(), 0);

            builder.setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_launcher_new_round)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(context, R.color.color_2));

            Notification notification = builder.build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Temperature Alert";
            String description = "Notification channel for temperature alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Set the color of the notification channel
            int color = ContextCompat.getColor(context, R.color.color_2);
            channel.enableLights(true);
            channel.setLightColor(color);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}