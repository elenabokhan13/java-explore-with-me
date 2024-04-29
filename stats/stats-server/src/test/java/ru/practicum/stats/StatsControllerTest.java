package ru.practicum.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stats.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    StatsService statsService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createHit() throws Exception {
        StatsDto statsDto = StatsDto.builder()
                .app("app-test")
                .uri("/test")
                .ip("0.0.0.0")
                .times(LocalDateTime.now())
                .build();

        when(statsService.createHit(any())).thenReturn(statsDto);

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(statsDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app").value(statsDto.getApp()))
                .andExpect(jsonPath("$.uri").value(statsDto.getUri()));
    }

    @Test
    void getStatsNotUnique() throws Exception {
        StatsCountDto count = StatsCountDto.builder()
                .uri("/test")
                .app("app-test")
                .hits(2L)
                .build();

        when(statsService.getStats(any(), any(), any(), any())).thenReturn(List.of(count));

        mvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("start", "2000-01-01")
                        .param("end", "2025-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]uri").value(count.getUri()))
                .andExpect(jsonPath("$.[0]app").value(count.getApp()))
                .andExpect(jsonPath("$.[0]hits").value(count.getHits()));
    }
}