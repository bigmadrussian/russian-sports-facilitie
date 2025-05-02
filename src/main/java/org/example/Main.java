package org.example;

import java.io.InputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        InputStream is = Main.class.getClassLoader().getResourceAsStream("Объекты спорта.csv");
        List<SportObject> objects = CsvParser.parse(is);
        DatabaseHelper.createTables();
        DatabaseHelper.insertObjects(objects);

        Analysis.regionHistogram();
        Analysis.averageByRegion();
        Analysis.topRegions();
    }
}
