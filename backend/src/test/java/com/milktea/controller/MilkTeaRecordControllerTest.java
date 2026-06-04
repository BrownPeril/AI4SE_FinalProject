package com.milktea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milktea.dto.MilkTeaRecordRequest;
import com.milktea.dto.MilkTeaRecordResponse;
import com.milktea.service.MilkTeaRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MilkTeaRecordController.class)
class MilkTeaRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MilkTeaRecordService service;

    @Autowired
    private ObjectMapper objectMapper;

    private MilkTeaRecordResponse buildResponse() {
        MilkTeaRecordResponse r = new MilkTeaRecordResponse();
        r.setId(1L);
        r.setBrand("喜茶");
        r.setDrinkName("多肉葡萄");
        r.setPrice(new BigDecimal("25.00"));
        r.setConsumeDate(LocalDateTime.of(2026, 6, 1, 14, 30));
        r.setSugarLevel("半糖");
        r.setRating(4);
        return r;
    }

    @Test
    void shouldCreateRecord() throws Exception {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        request.setBrand("喜茶");
        request.setDrinkName("多肉葡萄");
        request.setPrice(new BigDecimal("25.00"));
        request.setConsumeDate(LocalDateTime.of(2026, 6, 1, 14, 30));

        when(service.createRecord(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/api/milk-tea-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.brand").value("喜茶"));
    }

    @Test
    void shouldGetRecordById() throws Exception {
        when(service.getRecordById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/milk-tea-records/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void shouldListRecords() throws Exception {
        Page<MilkTeaRecordResponse> page = new PageImpl<>(List.of(buildResponse()), PageRequest.of(0, 10), 1);
        when(service.listRecords(isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
            .thenReturn(page);

        mockMvc.perform(get("/api/milk-tea-records?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void shouldUpdateRecord() throws Exception {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        request.setBrand("茶百道");
        request.setDrinkName("豆乳玉麒麟");
        request.setPrice(new BigDecimal("18.00"));
        request.setConsumeDate(LocalDateTime.of(2026, 6, 2, 10, 0));

        when(service.updateRecord(eq(1L), any())).thenReturn(buildResponse());

        mockMvc.perform(put("/api/milk-tea-records/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldDeleteRecord() throws Exception {
        mockMvc.perform(delete("/api/milk-tea-records/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldReturn400WhenValidationFails() throws Exception {
        MilkTeaRecordRequest request = new MilkTeaRecordRequest();
        // brand is null — should fail validation

        mockMvc.perform(post("/api/milk-tea-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
