package com.milktea.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "milk_tea_records")
public class MilkTeaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String drinkName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime consumeDate;

    @Column
    private String sugarLevel;

    @Column
    private String iceLevel;

    @Column
    private String cupSize;

    @Column
    private String teaBase;

    @Column
    private Integer rating;

    @Column(length = 500)
    private String comment;

    @Column
    private Boolean wouldRecommend;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getDrinkName() { return drinkName; }
    public void setDrinkName(String drinkName) { this.drinkName = drinkName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDateTime getConsumeDate() { return consumeDate; }
    public void setConsumeDate(LocalDateTime consumeDate) { this.consumeDate = consumeDate; }
    public String getSugarLevel() { return sugarLevel; }
    public void setSugarLevel(String sugarLevel) { this.sugarLevel = sugarLevel; }
    public String getIceLevel() { return iceLevel; }
    public void setIceLevel(String iceLevel) { this.iceLevel = iceLevel; }
    public String getCupSize() { return cupSize; }
    public void setCupSize(String cupSize) { this.cupSize = cupSize; }
    public String getTeaBase() { return teaBase; }
    public void setTeaBase(String teaBase) { this.teaBase = teaBase; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Boolean getWouldRecommend() { return wouldRecommend; }
    public void setWouldRecommend(Boolean wouldRecommend) { this.wouldRecommend = wouldRecommend; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
