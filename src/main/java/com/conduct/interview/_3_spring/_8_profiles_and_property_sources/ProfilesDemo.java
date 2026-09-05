package com.conduct.interview._3_spring._8_profiles_and_property_sources;

import org.springframework.context.support.GenericXmlApplicationContext;

public class ProfilesDemo {

    private static final String ACTIVE_PROFILE = "dev"; // flip to "prod" and re-run

    public static void main(String[] args) {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        // profile must be set before load(): <beans profile="..."> is evaluated during XML
        // parsing itself, not deferred until refresh()
        context.getEnvironment().setActiveProfiles(ACTIVE_PROFILE);
        context.load("classpath:com/conduct/interview/_3_spring/_8_profiles_and_property_sources/profiles-context.xml");
        context.refresh();

        DataSourceConfig config = context.getBean(DataSourceConfig.class);
        System.out.println("active profile: " + ACTIVE_PROFILE);
        System.out.println("resolved url  : " + config.getUrl());

        context.close();
    }
}
