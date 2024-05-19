package com.example.weather_backend.WeatherController;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DataController {
    private final WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<?> getWeatherForecast
            (@RequestParam("latitude") double latitude,
             @RequestParam("longitude") double longitude
            ) {
        boolean isDataValid = weatherService.validation(latitude, longitude);
        if(!isDataValid){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BadRequest
                            .builder()
                            .status("400")
                            .errorMessage("Invalid latitude or longitude")
                            .path("/api/weather")
                            .build()
            );
        }

        WeatherResponse weatherResponse = weatherService.apiConnection(latitude, longitude);
        if (weatherResponse != null) {
            return ResponseEntity.ok(weatherResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BadRequest
                            .builder()
                            .status("404")
                            .errorMessage("Weather data not found")
                            .path("/api/weather")
                            .build()
            );
        }
    }
}
