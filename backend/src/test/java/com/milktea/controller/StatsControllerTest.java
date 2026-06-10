package com.milktea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.milktea.dto.BrandDistributionResponse;
import com.milktea.dto.DailyTrendResponse;
import com.milktea.dto.StatsOverviewResponse;
import com.milktea.dto.WeeklySummaryResponse;
import com.milktea.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StatsService service;

    @InjectMocks
    private StatsController controller;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(converter)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldGetOverview() throws Exception {
        when(service.getOverview()).thenReturn(
            new StatsOverviewResponse(new BigDecimal("65.00"), 3L, new BigDecimal("21.67"), "喜茶"));

        mockMvc.perform(get("/api/stats/overview"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.totalSpent").value(65.00))
            .andExpect(jsonPath("$.data.totalCount").value(3))
            .andExpect(jsonPath("$.data.topBrand").value("喜茶"));
    }

    @Test
    void shouldGetDailyTrend() throws Exception {
        DailyTrendResponse resp = new DailyTrendResponse();
        resp.setDates(List.of("2026-06-01"));
        resp.setAmounts(List.of(new BigDecimal("25.00")));
        when(service.getDailyTrend(30)).thenReturn(resp);

        mockMvc.perform(get("/api/stats/daily-trend?days=30"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.dates").isArray());
    }

    @Test
    void shouldGetBrandDistribution() throws Exception {
        BrandDistributionResponse resp = new BrandDistributionResponse();
        resp.setBrands(List.of("喜茶", "茶百道"));
        resp.setCounts(List.of(2L, 1L));
        resp.setAmounts(List.of(new BigDecimal("47.00"), new BigDecimal("18.00")));
        when(service.getBrandDistribution()).thenReturn(resp);

        mockMvc.perform(get("/api/stats/brand-distribution"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.brands").isArray());
    }

    @Test
    void shouldGetWeeklySummary() throws Exception {
        WeeklySummaryResponse resp = new WeeklySummaryResponse();
        resp.setWeeks(List.of("06-02"));
        resp.setAmounts(List.of(new BigDecimal("65.00")));
        resp.setCounts(List.of(3L));
        when(service.getWeeklySummary(4)).thenReturn(resp);

        mockMvc.perform(get("/api/stats/weekly-summary?weeks=4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.weeks").isArray());
    }
}
