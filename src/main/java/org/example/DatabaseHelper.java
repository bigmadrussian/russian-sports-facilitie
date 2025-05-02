package org.example;

import java.sql.*;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "пароль";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTables() throws SQLException {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            // Устанавливаем схему public как активную
            stmt.execute("SET search_path TO public");

            // Удалим таблицы, если они есть
            stmt.execute("DROP TABLE IF EXISTS public.sport_objects");
            stmt.execute("DROP TABLE IF EXISTS public.regions");

            // Создаём таблицы заново
            stmt.execute("""
                CREATE TABLE public.regions (
                    id SERIAL PRIMARY KEY,
                    name TEXT UNIQUE NOT NULL
                )
                """);

            stmt.execute("""
                CREATE TABLE public.sport_objects (
                    id INTEGER PRIMARY KEY,
                    name TEXT,
                    region_id INTEGER REFERENCES public.regions(id),
                    address TEXT,
                    register_date TEXT
                )
                """);
        }
    }

    public static void insertObjects(List<SportObject> objects) throws SQLException {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            PreparedStatement psRegion = conn.prepareStatement(
                    "INSERT INTO public.regions(name) VALUES (?) ON CONFLICT(name) DO NOTHING",
                    Statement.RETURN_GENERATED_KEYS
            );

            PreparedStatement psRegionId = conn.prepareStatement("SELECT id FROM public.regions WHERE name = ?");

            PreparedStatement psObject = conn.prepareStatement(
                    "INSERT INTO public.sport_objects(id, name, region_id, address, register_date) VALUES (?, ?, ?, ?, ?)");

            for (SportObject obj : objects) {
                String region = obj.getRegion();

                psRegion.setString(1, region);
                psRegion.executeUpdate();

                psRegionId.setString(1, region);
                ResultSet rs = psRegionId.executeQuery();
                int regionId = rs.next() ? rs.getInt("id") : 0;

                psObject.setInt(1, obj.getId());
                psObject.setString(2, obj.getName());
                psObject.setInt(3, regionId);
                psObject.setString(4, obj.getAddress());
                psObject.setString(5, obj.getRegisterDate());
                psObject.addBatch();
            }

            psObject.executeBatch();
            conn.commit();
        }
    }
}
