package com.example.weather_backend.WeatherController;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {
    List<Double> maxTemperature;
    List<Double> minTemperature;
    List<Double> panelPower;
    List<String> date;
    List<Integer> weatherCode;

}
