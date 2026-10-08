package com.example.weatherapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.widget.SearchView;
import retrofit2.converter.gson.GsonConverterFactory;

//9187f46c8f8afbad79e1778ffad509e7

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchWeatherData("Jaipur");
        SearchCity();
    }

    private void SearchCity() {

        SearchView searchView = binding.searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    fetchWeatherData(query);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    private void fetchWeatherData(String cityName) {

        ApiInterface retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build()
                .create(ApiInterface.class);

        Call<WeatherApp> response = retrofit.getWeatherData(
                cityName,
                "9187f46c8f8afbad79e1778ffad509e7",
                "metric"
        );

        response.enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {

                WeatherApp responseBody = response.body();

                if (response.isSuccessful() && responseBody != null) {

                    String temperature = String.valueOf(responseBody.getMain().getTemp());
                    String humidity = String.valueOf(responseBody.getMain().getHumidity());
                    String windSpeed = String.valueOf(responseBody.getWind().getSpeed());
                    long sunRise = responseBody.getSys().getSunrise();
                    long sunSet = responseBody.getSys().getSunset();
                    String seaLevel = String.valueOf(responseBody.getMain().getPressure());
                    String condition = responseBody.getWeather().get(0).getMain();
                    String maxTemp = String.valueOf(responseBody.getMain().getTemp_max());
                    String minTemp = String.valueOf(responseBody.getMain().getTemp_min());

                    binding.temp.setText(temperature + " °C");
                    binding.weather.setText(condition);
                    binding.maxTemp.setText("Max Temp: " + maxTemp + " °C");
                    binding.minTemp.setText("Min Temp: " + minTemp + " °C");
                    binding.humidity.setText(humidity + " %");
                    binding.windspeed.setText(windSpeed + " m/s");
                    binding.sunRise.setText(time(sunRise));
                    binding.sunset.setText(time(sunSet));
                    binding.sea.setText(seaLevel + " hPa");
                    binding.condition.setText(condition);
                    binding.day.setText(dayName(System.currentTimeMillis()));
                    binding.date.setText(date());
                    binding.cityName.setText(cityName);

                    changeImageAccordingToWeatherCondition(condition);
                }
            }

            private void changeImageAccordingToWeatherCondition(String condition) {
                switch (condition) {

                    case "Clear":
                        binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
                        binding.lottieAnimationView.setAnimation(R.raw.sun);
                        break;

                    case "Clouds":
                    case "Mist":
                    case "Fog":
                    case "Haze":
                    case "Smoke":
                        binding.getRoot().setBackgroundResource(R.drawable.colud_background);
                        binding.lottieAnimationView.setAnimation(R.raw.cloud);
                        break;

                    case "Rain":
                    case "Drizzle":
                        binding.getRoot().setBackgroundResource(R.drawable.rain_background);
                        binding.lottieAnimationView.setAnimation(R.raw.rain);
                        break;

                    case "Snow":
                        binding.getRoot().setBackgroundResource(R.drawable.snow_background);
                        binding.lottieAnimationView.setAnimation(R.raw.snow);
                        break;

                    case "Thunderstorm":
                        binding.getRoot().setBackgroundResource(R.drawable.rain_background);
                        binding.lottieAnimationView.setAnimation(R.raw.rain);
                        break;

                    default:
                        binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
                        binding.lottieAnimationView.setAnimation(R.raw.sun);
                        break;
                }

                binding.lottieAnimationView.playAnimation();
            }

            @Override
            public void onFailure(Call<WeatherApp> call, Throwable t) {

            }
        });
    }

    private String dayName(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private String time(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }

    private String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}