package com.milktea.controller;

import com.milktea.dto.*;
import com.milktea.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<StatsOverviewResponse>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success(service.getOverview()));
    }

    @GetMapping("/daily-trend")
    public ResponseEntity<ApiResponse<DailyTrendResponse>> getDailyTrend(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(service.getDailyTrend(days)));
    }

    @GetMapping("/brand-distribution")
    public ResponseEntity<ApiResponse<BrandDistributionResponse>> getBrandDistribution() {
        return ResponseEntity.ok(ApiResponse.success(service.getBrandDistribution()));
    }

    @GetMapping("/weekly-summary")
    public ResponseEntity<ApiResponse<WeeklySummaryResponse>> getWeeklySummary(
            @RequestParam(defaultValue = "4") int weeks) {
        return ResponseEntity.ok(ApiResponse.success(service.getWeeklySummary(weeks)));
    }
}
