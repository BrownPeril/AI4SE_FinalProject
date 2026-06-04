package com.milktea.service;

import com.milktea.dto.MilkTeaRecordRequest;
import com.milktea.dto.MilkTeaRecordResponse;
import com.milktea.model.MilkTeaRecord;
import com.milktea.repository.MilkTeaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MilkTeaRecordServiceTest {

    @Mock
    private MilkTeaRecordRepository repository;

    @InjectMocks
    private MilkTeaRecordService service;

    private MilkTeaRecord sampleRecord;

    @BeforeEach
    void setUp() {
        sampleRecord = new MilkTeaRecord();
        sampleRecord.setId(1L);
        sampleRecord.setBrand("喜茶");
        sampleRecord.setDrinkName("多肉葡萄");
        sampleRecord.setPrice(new BigDecimal("25.00"));
        sampleRecord.setConsumeDate(LocalDateTime.of(2026, 6, 1, 14, 30));
        sampleRecord.setSugarLevel("半糖");
        sampleRecord.setRating(4);
    }

    private MilkTeaRecordRequest buildRequest() {
        MilkTeaRecordRequest req = new MilkTeaRecordRequest();
        req.setBrand("喜茶");
        req.setDrinkName("多肉葡萄");
        req.setPrice(new BigDecimal("25.00"));
        req.setConsumeDate(LocalDateTime.of(2026, 6, 1, 14, 30));
        req.setSugarLevel("半糖");
        req.setRating(4);
        return req;
    }

    @Test
    void shouldCreateRecord() {
        when(repository.save(any(MilkTeaRecord.class))).thenReturn(sampleRecord);
        MilkTeaRecordResponse response = service.createRecord(buildRequest());
        assertNotNull(response);
        assertEquals("喜茶", response.getBrand());
        assertEquals("多肉葡萄", response.getDrinkName());
        verify(repository).save(any(MilkTeaRecord.class));
    }

    @Test
    void shouldGetRecordById() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        MilkTeaRecordResponse response = service.getRecordById(1L);
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowWhenRecordNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getRecordById(999L));
    }

    @Test
    void shouldUpdateRecord() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        when(repository.save(any(MilkTeaRecord.class))).thenReturn(sampleRecord);

        MilkTeaRecordRequest updateReq = buildRequest();
        updateReq.setBrand("茶百道");
        MilkTeaRecordResponse response = service.updateRecord(1L, updateReq);
        assertNotNull(response);
        verify(repository).save(any(MilkTeaRecord.class));
    }

    @Test
    void shouldDeleteRecord() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);
        service.deleteRecord(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeleteNonExistent() {
        when(repository.existsById(999L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteRecord(999L));
    }

    @Test
    void shouldListRecordsWithFilters() {
        Page<MilkTeaRecord> page = new PageImpl<>(List.of(sampleRecord));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        Page<MilkTeaRecordResponse> result = service.listRecords("喜茶", null, null, null, null, 0, 10);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldListRecordsWithoutFilters() {
        Page<MilkTeaRecord> page = new PageImpl<>(List.of(sampleRecord));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        Page<MilkTeaRecordResponse> result = service.listRecords(null, null, null, null, null, 0, 10);
        assertEquals(1, result.getTotalElements());
    }
}
