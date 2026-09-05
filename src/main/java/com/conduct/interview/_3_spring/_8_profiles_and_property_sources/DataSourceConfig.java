package com.conduct.interview._3_spring._8_profiles_and_property_sources;

public class DataSourceConfig {

    private final String url;

    public DataSourceConfig(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
