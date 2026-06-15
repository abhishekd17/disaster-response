package com.disaster;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "Disaster Response Platform API",
        version = "1.0",
        description = "Real-time crowdsourced disaster reporting and response coordination platform with geospatial search, WebSocket notifications, and community verification.",
        contact = @Contact(name = "Abhishek Kumawat", url = "https://github.com/abhishekd17")
    )
)
public class DisasterResponseApplication {
    public static void main(String[] args) {
        SpringApplication.run(DisasterResponseApplication.class, args);
    }
}
