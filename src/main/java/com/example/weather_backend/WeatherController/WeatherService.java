package com.example.weather_backend.WeatherController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final ObjectMapper objectMapper;

    public WeatherResponse apiConnection(double latitude, double longitude){
        String URL = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                "&longitude=" + longitude + "&daily=weather_code,temperature_2m_max,temperature_2m_min,sunshine_duration&timezone=Europe%2FBerlin";
        HttpURLConnection apiConnection = fetchApiResponse(URL);
        try {
            if (apiConnection != null) {
                JsonNode response = objectMapper.readTree(apiConnection.getInputStream());
                logger.info("Data acquisition completed successfully");
                return parser(response);
            }
        }
        catch (IOException e){
            logger.error("Exception occurred while getting the date");
        }
        return null;
    }
    public boolean validation(double latitude, double longitude){

        if(latitude > 90 || latitude < -90){
            logger.error("Invalid latitude");
            return false;
        }
        if(longitude > 180 || longitude < -180){
            logger.error("Invalid longitude");
            return false;
        }
        return true;
    }
    private WeatherResponse parser(JsonNode response){
        List<Double> maxTemperature = new ArrayList<>();
        List<Double> minTemperature = new ArrayList<>();
        List<Double> panelPower = new ArrayList<>();
        List<String> date = new ArrayList<>();
        List<Integer> weatherCode = new ArrayList<>();

        JsonNode daily = response.get("daily");
        int dataSize = daily.get("time").size();

        for (int i=0; i<dataSize; i++) {
            maxTemperature.add(daily.get("temperature_2m_max").get(i).asDouble());
            minTemperature.add(daily.get("temperature_2m_min").get(i).asDouble());
            panelPower.add(calculatePanelPower(daily.get("sunshine_duration").get(i).asDouble()));
            date.add(daily.get("time").get(i).asText());
            weatherCode.add(daily.get("weather_code").get(i).asInt());
        }

        return WeatherResponse.builder()
                .maxTemperature(maxTemperature)
                .minTemperature(minTemperature)
                .panelPower(panelPower)
                .date(date)
                .weatherCode(weatherCode)
                .build();
    }

    private Double calculatePanelPower(double sunshineDuration) {
        double installationPower = 2.5; //[kw]
        double panelEfficiency = 0.2;
        sunshineDuration = sunshineDuration/3600; // [s->h]
        return installationPower*panelEfficiency*sunshineDuration;
    }


    private HttpURLConnection fetchApiResponse(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            logger.info("Connection set successfully");
            return connection;
        }
        catch (IOException e){
            logger.error("Error while connecting to open-meteo API");
        }
        return null;
    }
}
