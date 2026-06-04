package com.milktea.repository;

import com.milktea.model.MilkTeaRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MilkTeaRecordRepositoryTest {

    @Autowired
    private MilkTeaRecordRepository repository;

    private MilkTeaRecord createSampleRecord() {
        MilkTeaRecord record = new MilkTeaRecord();
        record.setBrand("喜茶");
        record.setDrinkName("多肉葡萄");
        record.setPrice(new BigDecimal("25.00"));
        record.setConsumeDate(LocalDateTime.of(2026, 6, 1, 14, 30));
        record.setSugarLevel("半糖");
        record.setIceLevel("少冰");
        record.setCupSize("大杯");
        record.setTeaBase("绿茶");
        record.setRating(4);
        record.setComment("好喝");
        record.setWouldRecommend(true);
        return record;
    }

    @Test
    void shouldSaveAndFindById() {
        MilkTeaRecord saved = repository.save(createSampleRecord());
        assertNotNull(saved.getId());
        assertTrue(repository.findById(saved.getId()).isPresent());
    }

    @Test
    void shouldFindByBrand() {
        repository.save(createSampleRecord());
        Page<MilkTeaRecord> result = repository.findByBrand("喜茶", PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindByBrandNotMatch() {
        repository.save(createSampleRecord());
        Page<MilkTeaRecord> result = repository.findByBrand("茶百道", PageRequest.of(0, 10));
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldFindByBrandContainingOrDrinkNameContaining() {
        repository.save(createSampleRecord());
        Page<MilkTeaRecord> result = repository.findByBrandContainingOrDrinkNameContaining("葡萄", "葡萄", PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindByPriceBetween() {
        repository.save(createSampleRecord());
        Page<MilkTeaRecord> result = repository.findByPriceBetween(
            new BigDecimal("20"), new BigDecimal("30"), PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindBySugarLevel() {
        repository.save(createSampleRecord());
        Page<MilkTeaRecord> result = repository.findBySugarLevel("半糖", PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldDeleteRecord() {
        MilkTeaRecord saved = repository.save(createSampleRecord());
        repository.deleteById(saved.getId());
        assertFalse(repository.findById(saved.getId()).isPresent());
    }
}
