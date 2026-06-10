package com.milktea.controller;

import com.milktea.dto.ApiResponse;
import com.milktea.dto.MilkTeaRecordRequest;
import com.milktea.dto.MilkTeaRecordResponse;
import com.milktea.service.MilkTeaRecordService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/milk-tea-records")
public class MilkTeaRecordController {

    private final MilkTeaRecordService service;

    public MilkTeaRecordController(MilkTeaRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MilkTeaRecordResponse>> createRecord(
            @Valid @RequestBody MilkTeaRecordRequest request) {
        MilkTeaRecordResponse response = service.createRecord(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MilkTeaRecordResponse>> getRecordById(@PathVariable Long id) {
        MilkTeaRecordResponse response = service.getRecordById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MilkTeaRecordResponse>> updateRecord(
            @PathVariable Long id, @Valid @RequestBody MilkTeaRecordRequest request) {
        MilkTeaRecordResponse response = service.updateRecord(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        service.deleteRecord(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MilkTeaRecordResponse>>> listRecords(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String sugarLevel,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MilkTeaRecordResponse> result = service.listRecords(
            brand, sugarLevel, minPrice, maxPrice, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
