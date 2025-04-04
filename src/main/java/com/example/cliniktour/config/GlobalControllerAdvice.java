package com.example.cliniktour.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;



@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("request")
    public HttpServletRequest addRequest(HttpServletRequest request) {
        return request;
    }
}