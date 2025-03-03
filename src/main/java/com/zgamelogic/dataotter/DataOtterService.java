package com.zgamelogic.dataotter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.dataotter.data.DataOtterRockEvent;
import com.zgamelogic.dataotter.data.Monitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@EnableAsync
@Async("dataOtterServiceExecutor")
public class DataOtterService {
    private final String dataotterUrl;
    private final long appid;
    private final boolean enabled;
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    public DataOtterService(
            @Value("${dataotter.url}") String dataotterUrl,
            @Value("${dataotter.appid}") long appid,
            @Value("${dataotter.enabled:true}") boolean enabled
    ) {
        this.dataotterUrl = dataotterUrl;
        this.appid = appid;
        this.enabled = enabled;
        restTemplate = new RestTemplate();
        httpHeaders = new HttpHeaders();
        httpHeaders.add("api-key", 0 + "");
        if(!enabled) {
            log.warn("DataOtter service is currently disabled. Rocks will not be tracked");
        }
    }

    @EventListener
    void handleRockEvent(DataOtterRockEvent event) {
        sendRock(event.getPebble());
    }

    /**
     * Sends a rock with a pebble to DataOtter.
     * @param pebble String or object containing data
     */
    public void sendRock(Object pebble){
        if(pebble == null || !enabled) return;
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
            String url = dataotterUrl + "/rocks/" + appid;
            HttpEntity<String> requestEntity = new HttpEntity<>(payload, httpHeaders);
            restTemplate.postForObject(url, requestEntity, String.class);
        } catch (RestClientException e) {
            log.error("Unable to send rock", e);
        }
    }

    /**
     * Gets a list of monitors and their statuses
     * @return List of monitors with their statuses
     */
    @Async
    public CompletableFuture<List<Monitor>> getMonitorsStatus(){
        String URL = dataotterUrl + "/monitors?include-status=true";
        try {
            return CompletableFuture.completedFuture(List.of(restTemplate.getForObject(new URI(URL), Monitor[].class)));
        } catch (Exception e) {
            log.error("Error fetching monitors", e);
        }
        return CompletableFuture.completedFuture(List.of());
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
