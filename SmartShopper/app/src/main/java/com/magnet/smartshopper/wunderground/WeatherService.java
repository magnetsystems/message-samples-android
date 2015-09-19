package com.magnet.smartshopper.wunderground;



import retrofit.RestAdapter;

import com.magnet.smartshopper.wunderground.model.Weather;
import com.magnet.smartshopper.wunderground.model.WeatherResponse;


public class WeatherService {

    public static final String BASE_URL = "http://api.wunderground.com/";

    private static RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
            .build();

    public static Weather getCurrentTemperature() {

        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        WeatherServiceClient apiService =
                restAdapter.create(WeatherServiceClient.class);
        WeatherResponse response = apiService.getCurrentWeather();
        Weather weather = new Weather();
        weather.setTemp(response.getCurrent_observation().getTemp_f().toString());
        weather.setIconUrl(response.getCurrent_observation().getIcon_url());

        return weather;


    }


}
