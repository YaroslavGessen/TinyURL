package com.yaroslav.tinyurl.config;

import com.datastax.oss.driver.api.core.CqlSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CassandraConfig {
    private static final String SECURE_CONNECT = "/secure-connect-yaroslavdb.zip";
    private static final String USERNAME = "ygessen";
    private static final String PASSWORD = "AZSo5088";
    private static final String KEYSPACE = "clicks";

    @Bean("cassandraSession")
    public CqlSession getSession () {
        return CqlSession.builder()
                .withCloudSecureConnectBundle(getClass().getResourceAsStream(SECURE_CONNECT))
                .withAuthCredentials(USERNAME, PASSWORD)
                .withKeyspace(KEYSPACE)
                .build();
    }
}
