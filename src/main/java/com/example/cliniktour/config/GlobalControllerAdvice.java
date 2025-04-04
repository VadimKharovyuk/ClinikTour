package com.example.cliniktour.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice
public class GlobalControllerAdvice {

    private final String baseUrl;

    // Конструктор с параметром, который можно настроить в application.properties
    public GlobalControllerAdvice(@Value("${site.base-url:https://asiamedtour.com}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @ModelAttribute("request")
    public HttpServletRequest addRequest(HttpServletRequest request) {
        return request;
    }

    @ModelAttribute("baseUrl")
    public String addBaseUrl() {
        return baseUrl;
    }
}