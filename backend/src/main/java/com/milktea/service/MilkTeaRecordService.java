package com.milktea.service;

import com.milktea.dto.MilkTeaRecordRequest;
import com.milktea.dto.MilkTeaRecordResponse;
import com.milktea.model.MilkTeaRecord;
import com.milktea.repository.MilkTeaRecordRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MilkTeaRecordService {

    private final MilkTeaRecordRepository repository;

    public MilkTeaRecordService(MilkTeaRecordRepository repository) {
        this.repository = repository;
    }

    public MilkTeaRecordResponse createRecord(MilkTeaRecordRequest request) {
        MilkTeaRecord record = new MilkTeaRecord();
        BeanUtils.copyProperties(request, record);
        MilkTeaRecord saved = repository.save(record);
        return toResponse(saved);
    }

    public MilkTeaRecordResponse getRecordById(Long id) {
        MilkTeaRecord record = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("记录不存在"));
        return toResponse(record);
    }

    public MilkTeaRecordResponse updateRecord(Long id, MilkTeaRecordRequest request) {
        MilkTeaRecord record = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("记录不存在"));
        BeanUtils.copyProperties(request, record);
        record.setId(id);
        MilkTeaRecord saved = repository.save(record);
        return toResponse(saved);
    }

    public void deleteRecord(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("记录不存在");
        }
        repository.deleteById(id);
    }

    public Page<MilkTeaRecordResponse> listRecords(
            String brand, String sugarLevel, BigDecimal minPrice,
            BigDecimal maxPrice, String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "consumeDate"));

        boolean hasFilter = brand != null || sugarLevel != null
            || minPrice != null || maxPrice != null || keyword != null;

        Page<MilkTeaRecord> records;
        if (hasFilter) {
            Specification<MilkTeaRecord> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (brand != null && !brand.isBlank()) {
                    predicates.add(cb.equal(root.get("brand"), brand));
                }
                if (sugarLevel != null && !sugarLevel.isBlank()) {
                    predicates.add(cb.equal(root.get("sugarLevel"), sugarLevel));
                }
                if (minPrice != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
                }
                if (keyword != null && !keyword.isBlank()) {
                    String pattern = "%" + keyword + "%";
                    predicates.add(cb.or(
                        cb.like(root.get("brand"), pattern),
                        cb.like(root.get("drinkName"), pattern)
                    ));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            records = repository.findAll(spec, pageable);
        } else {
            records = repository.findAll(pageable);
        }

        return records.map(this::toResponse);
    }

    private MilkTeaRecordResponse toResponse(MilkTeaRecord record) {
        MilkTeaRecordResponse response = new MilkTeaRecordResponse();
        BeanUtils.copyProperties(record, response);
        return response;
    }
}
