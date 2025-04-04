package com.example.cliniktour.util;

import com.example.cliniktour.model.IPVisit;
import com.example.cliniktour.service.IPVisitService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IPTrackingFilter extends HttpFilter {

    private final IPVisitService ipVisitService;

    // Используем ConcurrentHashMap для более эффективной работы с ключами "IP+Path"
    private final Map<String, Long> requestCache = new ConcurrentHashMap<>();

    @Autowired
    public IPTrackingFilter(IPVisitService ipVisitService) {
        this.ipVisitService = ipVisitService;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {

        String path = request.getRequestURI();

        // Игнорировать запросы на статические файлы и ресурсы
        if (path.startsWith("/css/") || path.startsWith("/images/") ||
                path.startsWith("/js/") || path.startsWith("/favicon.ico") ||
                path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }

        // Получаем реальный IP-адрес
        String ip = getClientIpAddress(request);

        // Создаем уникальный ключ для запроса (IP + путь)
        String requestKey = ip + ":" + path;

        // Текущее время в миллисекундах
        long currentTime = System.currentTimeMillis();

        // Проверяем, не был ли этот запрос недавно обработан
        Long lastRequestTime = requestCache.get(requestKey);
        boolean isDuplicate = lastRequestTime != null && (currentTime - lastRequestTime) < 3000; // 5 секунд

        // Обновляем кэш даже для дубликатов, чтобы продлить время блокировки
        requestCache.put(requestKey, currentTime);

        // Очистка старых записей каждые 100 запросов
        if (requestCache.size() > 1000) {
            cleanupCache();
        }

        // Если запрос не дублирующийся, логируем его
        if (!isDuplicate) {
            // Получаем геолокацию
            String location = getLocation(ip);

            // Логируем в консоль
            System.out.println("[" + LocalDateTime.now() + "] " +
                    "Request: " + request.getMethod() + " " + path + " from IP: " + ip + " (" + location + ")");

            // Сохраняем в базу данных
            ipVisitService.logVisit(ip, location);
        }

        chain.doFilter(request, response);
    }

    // Очистка старых записей в кэше
    private void cleanupCache() {
        long currentTime = System.currentTimeMillis();
        long cutoffTime = currentTime - 30000; // Удаляем записи старше 30 секунд

        requestCache.entrySet().removeIf(entry -> entry.getValue() < cutoffTime);
    }

    // Метод получения реального IP с учетом прокси
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = null;
        String[] ipHeaders = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : ipHeaders) {
            ipAddress = request.getHeader(header);
            if (ipAddress != null && ipAddress.length() > 0 && !"unknown".equalsIgnoreCase(ipAddress)) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
                break;
            }
        }

        if (ipAddress == null || ipAddress.length() == 0) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
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

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Unknown location";
            }

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