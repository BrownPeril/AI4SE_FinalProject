package com.milktea.repository;

import com.milktea.model.MilkTeaRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface MilkTeaRecordRepository extends JpaRepository<MilkTeaRecord, Long> {

    Page<MilkTeaRecord> findByBrand(String brand, Pageable pageable);

    Page<MilkTeaRecord> findBySugarLevel(String sugarLevel, Pageable pageable);

    Page<MilkTeaRecord> findByBrandContainingOrDrinkNameContaining(
        String brandKeyword, String drinkNameKeyword, Pageable pageable);

    Page<MilkTeaRecord> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
