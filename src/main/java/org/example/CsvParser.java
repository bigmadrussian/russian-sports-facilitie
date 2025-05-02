package org.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public static List<SportObject> parse(InputStream inputStream) {
        List<SportObject> objects = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "CP1251"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (tokens.length < 5) continue;
                objects.add(new SportObject(
                        Integer.parseInt(tokens[0].replaceAll("\\D", "")),
                        tokens[1].replaceAll("\"", ""),
                        tokens[2].replaceAll("\"", ""),
                        tokens[3].replaceAll("\"", ""),
                        tokens[4].replaceAll("\"", "")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }
}

