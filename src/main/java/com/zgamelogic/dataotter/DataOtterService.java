package com.zgamelogic.dataotter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@Slf4j
@Service
@EnableAsync
@Async("dataOtterServiceExecutor")
public class DataOtterService {
    private final long appid;
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    public DataOtterService(
            @Value("${dataotter-appid}") long appid,
            @Value("${dataotter-api-key}") String apiKey
    ) {
        this.appid = appid;
        baseUrl = "http://20.40.218.161:8080";
        restTemplate = new RestTemplate();
        httpHeaders = new HttpHeaders();
        httpHeaders.add("api-key", apiKey);
    }

    /**
     * Sends a rock with a pebble to DataOtter.
     * @param pebble String or object containing data
     */
    public void sendRock(Object pebble){
        if(pebble == null) return;
        String payload;
        if(pebble instanceof String){
            payload = (String) pebble;
        } else {
            ObjectMapper om = new ObjectMapper();
            try {
                payload = om.writeValueAsString(pebble);
            } catch (JsonProcessingException e) {
                log.error("Unable to serialize object to JSON", e);
                return;
            }
        }
        try {
            String url = baseUrl + "/rocks/" + appid;
            HttpEntity<String> requestEntity = new HttpEntity<>(payload, httpHeaders);
            restTemplate.postForObject(url, requestEntity, String.class);
        } catch (RestClientException e) {
            log.error("Unable to send rock", e);
        }
    }

    private static class DataOtterExecutorConfig {
        @Bean(name = "dataOtterServiceExecutor")
        public Executor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(5);
            executor.setThreadNamePrefix("DataOtter-");
            executor.initialize();
            return executor;
        }
    }
}
