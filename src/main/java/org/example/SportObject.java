package org.example;

public class SportObject {
    private int id;
    private String name;
    private String region;
    private String address;
    private String registerDate;

    public SportObject(int id, String name, String region, String address, String registerDate) {
        this.id = id;
        this.name = name;
        this.region = normalizeRegion(region);
        this.address = address;
        this.registerDate = registerDate;
    }

    private String normalizeRegion(String region) {
        if (region.contains("Москва")) return "Москва и МО";
        if (region.contains("Московская область")) return "Москва и МО";
        return region;
    }

    public String getRegion() { return region; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getRegisterDate() { return registerDate; }
}
