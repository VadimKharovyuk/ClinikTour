package com.example.cliniktour.service;

import com.example.cliniktour.model.IPVisit;
import com.example.cliniktour.repository.IPVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class IPVisitService {

    private final IPVisitRepository ipVisitRepository;

    @Autowired
    public IPVisitService(IPVisitRepository ipVisitRepository) {
        this.ipVisitRepository = ipVisitRepository;
    }

    // Метод для логирования посещений с геолокацией
    public void logVisit(String ipAddress, String location) {
        IPVisit ipVisit = ipVisitRepository.findByIpAddress(ipAddress);
        if (ipVisit == null) {
            ipVisit = new IPVisit(ipAddress, location);
        } else {
            ipVisit.incrementVisitCount();

            // Обновляем геолокацию, если она изменилась
            if (location != null && !location.equals("Unknown location") &&
                    (ipVisit.getLocation() == null || ipVisit.getLocation().equals("Unknown location"))) {
                ipVisit.setLocation(location);
            }
        }
        ipVisitRepository.save(ipVisit);
    }

    // Метод для получения всех IP-адресов с количеством посещений
    public List<IPVisit> getAllVisits() {
        return ipVisitRepository.findAll(Sort.by(Sort.Direction.DESC, "lastVisit"));
    }

    // Метод для получения последних посещений
    public List<IPVisit> getRecentVisits(int limit) {
        if (limit == 20) {
            return ipVisitRepository.findTop20ByOrderByLastVisitDesc();
        } else {
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastVisit"));
            return ipVisitRepository.findAll(pageable).getContent();
        }
    }

    // Метод для получения статистики по посещениям
    public Map<String, Object> getVisitStats() {
        Map<String, Object> stats = new HashMap<>();

        Long totalVisits = ipVisitRepository.count();
        Long totalHits = ipVisitRepository.sumVisitCount();

        if (totalHits == null) {
            totalHits = 0L;
        }

        stats.put("uniqueVisitors", totalVisits);
        stats.put("totalHits", totalHits);

        // Топ 10 стран по посещениям
        stats.put("topCountries", ipVisitRepository.findTopCountries(10));

        // Посещения по дням за последние 14 дней
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -14);
        stats.put("dailyVisits", ipVisitRepository.findDailyVisitsBetween(cal.getTime()));

        // Средние посещения в день
        Double avgDailyVisits = ipVisitRepository.getAverageDailyVisits();
        stats.put("avgDailyVisits", avgDailyVisits != null ? avgDailyVisits : 0.0);

        // Посещения за сегодня
        Long todayVisits = ipVisitRepository.countVisitsToday();
        stats.put("todayVisits", todayVisits != null ? todayVisits : 0);

        return stats;
    }
    public Map<String, Long> getDailyVisitStats() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -14);

        List<IPVisit> recentVisits = ipVisitRepository.findVisitsSinceDate(cal.getTime());
        Map<String, Long> dailyStats = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (IPVisit visit : recentVisits) {
            String day = dateFormat.format(visit.getLastVisit());
            dailyStats.put(day, dailyStats.getOrDefault(day, 0L) + 1);
        }

        return dailyStats;
    }
}