package com.milktea.service;

import com.milktea.dto.*;
import com.milktea.model.MilkTeaRecord;
import com.milktea.repository.MilkTeaRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final MilkTeaRecordRepository repository;

    public StatsService(MilkTeaRecordRepository repository) {
        this.repository = repository;
    }

    public StatsOverviewResponse getOverview() {
        List<MilkTeaRecord> records = repository.findAll();
        if (records.isEmpty()) {
            return new StatsOverviewResponse(BigDecimal.ZERO, 0L, BigDecimal.ZERO, null);
        }

        BigDecimal totalSpent = records.stream()
            .map(MilkTeaRecord::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalCount = records.size();
        BigDecimal avgPrice = totalSpent.divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);

        String topBrand = records.stream()
            .collect(Collectors.groupingBy(MilkTeaRecord::getBrand, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        return new StatsOverviewResponse(totalSpent, totalCount, avgPrice, topBrand);
    }

    public DailyTrendResponse getDailyTrend(int days) {
        List<MilkTeaRecord> records = repository.findAll();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, BigDecimal> dailyMap = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            dailyMap.put(today.minusDays(i).format(fmt), BigDecimal.ZERO);
        }

        for (MilkTeaRecord r : records) {
            if (r.getConsumeDate() != null) {
                String dateKey = r.getConsumeDate().toLocalDate().format(fmt);
                dailyMap.merge(dateKey, r.getPrice(), BigDecimal::add);
            }
        }

        DailyTrendResponse response = new DailyTrendResponse();
        response.setDates(new ArrayList<>(dailyMap.keySet()));
        response.setAmounts(new ArrayList<>(dailyMap.values()));
        return response;
    }

    public BrandDistributionResponse getBrandDistribution() {
        List<MilkTeaRecord> records = repository.findAll();

        Map<String, Long> countMap = records.stream()
            .collect(Collectors.groupingBy(MilkTeaRecord::getBrand, Collectors.counting()));

        Map<String, BigDecimal> amountMap = records.stream()
            .collect(Collectors.groupingBy(
                MilkTeaRecord::getBrand,
                Collectors.reducing(BigDecimal.ZERO, MilkTeaRecord::getPrice, BigDecimal::add)
            ));

        List<String> brands = new ArrayList<>(countMap.keySet());
        brands.sort((a, b) -> countMap.get(b).compareTo(countMap.get(a)));

        BrandDistributionResponse response = new BrandDistributionResponse();
        response.setBrands(brands);
        response.setCounts(brands.stream().map(countMap::get).collect(Collectors.toList()));
        response.setAmounts(brands.stream().map(amountMap::get).collect(Collectors.toList()));
        return response;
    }

    public WeeklySummaryResponse getWeeklySummary(int weeks) {
        List<MilkTeaRecord> records = repository.findAll();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        WeekFields wf = WeekFields.of(java.util.Locale.CHINA);

        Map<String, BigDecimal> weeklyAmounts = new LinkedHashMap<>();
        Map<String, Long> weeklyCounts = new LinkedHashMap<>();

        for (int i = weeks - 1; i >= 0; i--) {
            LocalDate weekStart = today.minusWeeks(i).with(wf.dayOfWeek(), 1);
            String key = weekStart.format(fmt);
            weeklyAmounts.put(key, BigDecimal.ZERO);
            weeklyCounts.put(key, 0L);
        }

        for (MilkTeaRecord r : records) {
            if (r.getConsumeDate() != null) {
                LocalDate consumeDate = r.getConsumeDate().toLocalDate();
                LocalDate weekStart = consumeDate.with(wf.dayOfWeek(), 1);
                String key = weekStart.format(fmt);
                if (weeklyAmounts.containsKey(key)) {
                    weeklyAmounts.merge(key, r.getPrice(), BigDecimal::add);
                    weeklyCounts.merge(key, 1L, Long::sum);
                }
            }
        }

        WeeklySummaryResponse response = new WeeklySummaryResponse();
        response.setWeeks(new ArrayList<>(weeklyAmounts.keySet()));
        response.setAmounts(new ArrayList<>(weeklyAmounts.values()));
        response.setCounts(new ArrayList<>(weeklyCounts.values()));
        return response;
    }
}
