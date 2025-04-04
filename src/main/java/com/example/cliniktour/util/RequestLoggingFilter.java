package com.example.cliniktour.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
@Component
public class RequestLoggingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {

        String path = request.getRequestURI();

        // Игнорировать запросы на статические файлы
        if (path.startsWith("/css/") || path.startsWith("/images/") || path.startsWith("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        String method = request.getMethod();
        String ip = request.getRemoteAddr();

        String location = getLocation(ip);

        System.out.println("[" + LocalDateTime.now() + "] " +
                "Request: " + method + " " + path + " from IP: " + ip + " (" + location + ")");

        chain.doFilter(request, response);
    }

    private String getLocation(String ip) {
        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
            return "Localhost (Testing)";
        }

        try {
            URL url = new URL("http://ip-api.com/json/" + ip + "?fields=country,regionName,city");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream()))
                    .getAsJsonObject();

            String country = json.get("country").getAsString();
            String region = json.get("regionName").getAsString();
            String city = json.get("city").getAsString();

            return city + ", " + region + ", " + country;
        } catch (Exception e) {
            return "Unknown location";
        }
    }

}