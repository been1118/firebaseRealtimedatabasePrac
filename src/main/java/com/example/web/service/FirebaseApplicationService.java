package com.example.web.service;

import com.example.web.config.FirebaseConfigurationProperties;
import com.example.web.repository.DefaultFirebaseRealtimeDatabaseRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@EnableConfigurationProperties(FirebaseConfigurationProperties.class)
@RequiredArgsConstructor
public class FirebaseApplicationService {

    private GoogleCredential scoped;

    private final ResourceLoader resourceLoader;

    private final FirebaseConfigurationProperties firebaseConfigurationProperties;

    private final DefaultFirebaseRealtimeDatabaseRepository defaultFirebaseRealtimeDatabaseRepository;

    private String token() throws IOException {
        String token = scoped.getAccessToken();
        if (token == null || scoped.getExpiresInSeconds() < 100) {
            scoped.refreshToken();
            token = scoped.getAccessToken();
        }
        return token;
    }

    public MultiValueMap<String, String> headers() throws IOException {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token());
        return headers;
    }

    public String getDatabaseUrl() {
        return firebaseConfigurationProperties.getRealtimeDatabaseUrl();
    }

    @Transactional
    public void getFire(Long id) {
        defaultFirebaseRealtimeDatabaseRepository.push(id);
    }

}