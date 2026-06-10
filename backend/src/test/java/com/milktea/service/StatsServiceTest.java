package com.milktea.service;

import com.milktea.dto.BrandDistributionResponse;
import com.milktea.dto.DailyTrendResponse;
import com.milktea.dto.StatsOverviewResponse;
import com.milktea.dto.WeeklySummaryResponse;
import com.milktea.model.MilkTeaRecord;
import com.milktea.repository.MilkTeaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private MilkTeaRecordRepository repository;

    @InjectMocks
    private StatsService service;

    private List<MilkTeaRecord> sampleRecords;

    @BeforeEach
    void setUp() {
        MilkTeaRecord r1 = new MilkTeaRecord();
        r1.setBrand("喜茶");
        r1.setDrinkName("多肉葡萄");
        r1.setPrice(new BigDecimal("25.00"));
        r1.setConsumeDate(LocalDateTime.of(2026, 6, 1, 14, 30));

        MilkTeaRecord r2 = new MilkTeaRecord();
        r2.setBrand("茶百道");
        r2.setDrinkName("豆乳玉麒麟");
        r2.setPrice(new BigDecimal("18.00"));
        r2.setConsumeDate(LocalDateTime.of(2026, 6, 2, 10, 15));

        MilkTeaRecord r3 = new MilkTeaRecord();
        r3.setBrand("喜茶");
        r3.setDrinkName("满杯红柚");
        r3.setPrice(new BigDecimal("22.00"));
        r3.setConsumeDate(LocalDateTime.of(2026, 6, 3, 16, 0));

        sampleRecords = List.of(r1, r2, r3);
    }

    @Test
    void shouldReturnOverview() {
        when(repository.findAll()).thenReturn(sampleRecords);
        StatsOverviewResponse overview = service.getOverview();
        assertEquals(new BigDecimal("65.00"), overview.getTotalSpent());
        assertEquals(3, overview.getTotalCount());
        assertEquals(0, overview.getAvgPrice().compareTo(new BigDecimal("21.67")));
        assertEquals("喜茶", overview.getTopBrand());
    }

    @Test
    void shouldReturnZeroOverviewWhenNoData() {
        when(repository.findAll()).thenReturn(List.of());
        StatsOverviewResponse overview = service.getOverview();
        assertEquals(BigDecimal.ZERO, overview.getTotalSpent());
        assertEquals(0, overview.getTotalCount());
        assertNull(overview.getTopBrand());
    }

    @Test
    void shouldReturnDailyTrend() {
        when(repository.findAll()).thenReturn(sampleRecords);
        DailyTrendResponse trend = service.getDailyTrend(30);
        assertFalse(trend.getDates().isEmpty());
        assertFalse(trend.getAmounts().isEmpty());
        assertEquals(trend.getDates().size(), trend.getAmounts().size());
    }

    @Test
    void shouldReturnBrandDistribution() {
        when(repository.findAll()).thenReturn(sampleRecords);
        BrandDistributionResponse dist = service.getBrandDistribution();
        assertTrue(dist.getBrands().contains("喜茶"));
        assertTrue(dist.getBrands().contains("茶百道"));
        assertEquals(dist.getBrands().size(), dist.getCounts().size());
        assertEquals(dist.getBrands().size(), dist.getAmounts().size());
    }

    @Test
    void shouldReturnWeeklySummary() {
        when(repository.findAll()).thenReturn(sampleRecords);
        WeeklySummaryResponse summary = service.getWeeklySummary(4);
        assertFalse(summary.getWeeks().isEmpty());
        assertEquals(summary.getWeeks().size(), summary.getAmounts().size());
        assertEquals(summary.getWeeks().size(), summary.getCounts().size());
    }
}
