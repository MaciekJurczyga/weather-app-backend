package com.example.weather_backend.WeatherController;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BadRequest {
    String errorMessage;
    String status;
    String path;
}
