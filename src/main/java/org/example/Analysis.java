package org.example;

import java.sql.*;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.ChartUtils;
import org.jfree.data.category.DefaultCategoryDataset;

public class Analysis {
    public static void regionHistogram() throws Exception {
        Map<String, Integer> regionCounts = new HashMap<>();
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                 SELECT r.name, COUNT(*) 
                 FROM public.sport_objects s 
                 JOIN public.regions r ON s.region_id = r.id 
                 GROUP BY r.name
             """)) {
            while (rs.next()) {
                regionCounts.put(rs.getString(1), rs.getInt(2));
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : regionCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Количество", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Объекты спорта по регионам", "Регион", "Количество", dataset,
                PlotOrientation.VERTICAL, false, true, false);

        ChartUtils.saveChartAsPNG(new java.io.File("histogram.png"), chart, 1200, 800);
    }

    public static void averageByRegion() throws Exception {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                 SELECT COUNT(*) * 1.0 / COUNT(DISTINCT region_id) AS avg_count 
                 FROM public.sport_objects
             """)) {
            if (rs.next()) {
                System.out.printf("Среднее количество объектов спорта в регионах: %.2f\n", rs.getDouble(1));
            }
        }
    }

    public static void topRegions() throws Exception {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                 SELECT r.name, COUNT(*) AS cnt 
                 FROM public.sport_objects s 
                 JOIN public.regions r ON s.region_id = r.id 
                 GROUP BY r.name 
                 ORDER BY cnt DESC 
                 LIMIT 3
             """)) {
            System.out.println("Топ-3 регионов по числу объектов спорта:");
            while (rs.next()) {
                System.out.printf("%s — %d объектов\n", rs.getString(1), rs.getInt(2));
            }
        }
    }
}