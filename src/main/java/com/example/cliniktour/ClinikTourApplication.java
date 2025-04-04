package com.example.cliniktour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClinikTourApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinikTourApplication.class, args);
    }
//   docker-compose up -d --build
  //   ./mvnw clean package


    // Приложение будет доступно по адресу: http://localhost:2817


    //     docker-compose -f docker-compose.prod.yml up -d

    //______________________________________


   //# В application-prod.properties (для продакшена)
    //site.base-url=https://asiamedtour.com





}
