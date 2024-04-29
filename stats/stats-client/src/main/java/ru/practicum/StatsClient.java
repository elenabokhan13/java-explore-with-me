package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.StatsCountDto;
import ru.practicum.stats.StatsDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient {
    private final RestTemplate rest;

    @Autowired
    public StatsClient(RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090"))
                .build();
    }

    public ResponseEntity<StatsDto> postStats(StatsDto statsDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<StatsDto> response = rest.exchange("/hit", HttpMethod.POST,
                new HttpEntity<>(statsDto, headers), StatsDto.class);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        return responseBuilder.body(response.getBody());
    }

    public ResponseEntity<List<StatsCountDto>> getStats(String start, String end, @Nullable List<String> uris,
                                                        @Nullable String unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);

        if (uris != null) {
            StringBuilder uri = new StringBuilder();
            for (String x : uris) {
                uri.append(",").append(x);
            }
            parameters.put("uris", uri.toString());
        }
        if (unique != null) {
            parameters.put("unique", unique);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<List<StatsCountDto>> response = rest
                .exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET,
                        new HttpEntity<>(null, headers), new ParameterizedTypeReference<>() {
                        }, parameters);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        return responseBuilder.body(response.getBody());
    }
}
