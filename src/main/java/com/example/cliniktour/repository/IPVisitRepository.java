package com.example.cliniktour.repository;

import com.example.cliniktour.model.IPVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface IPVisitRepository extends JpaRepository<IPVisit, Long> {
    IPVisit findByIpAddress(String ipAddress);

    @Query("SELECT SUM(v.visitCount) FROM IPVisit v")
    Long sumVisitCount();

    @Query("SELECT SUBSTRING(v.location, LOCATE(', ', v.location, LOCATE(', ', v.location) + 1) + 2) as country, " +
            "COUNT(v) as visitorCount, SUM(v.visitCount) as hitCount " +
            "FROM IPVisit v " +
            "WHERE v.location IS NOT NULL AND v.location != 'Unknown location' " +
            "GROUP BY country " +
            "ORDER BY hitCount DESC")
    List<Object[]> findTopCountries(int limit);

    // Метод для получения посещений за сегодня
    @Query("SELECT COUNT(v) FROM IPVisit v WHERE FUNCTION('DATE', v.lastVisit) = CURRENT_DATE")
    Long countVisitsToday();

    // Метод для получения среднего количества посещений в день
    @Query("SELECT AVG(c.cnt) FROM (SELECT COUNT(v) as cnt FROM IPVisit v GROUP BY FUNCTION('DATE', v.lastVisit)) c")
    Double getAverageDailyVisits();

    @Query("SELECT FUNCTION('DATE', v.lastVisit) as day, COUNT(v) as visitCount " +
            "FROM IPVisit v " +
            "WHERE v.lastVisit >= :startDate " +
            "GROUP BY FUNCTION('DATE', v.lastVisit) " +
            "ORDER BY FUNCTION('DATE', v.lastVisit)")
    List<Object[]> findDailyVisitsBetween(@Param("startDate") Date startDate);

    List<IPVisit> findTop20ByOrderByLastVisitDesc();

    @Query("SELECT v FROM IPVisit v WHERE v.lastVisit >= :startDate ORDER BY v.lastVisit")
    List<IPVisit> findVisitsSinceDate(@Param("startDate") Date startDate);
}