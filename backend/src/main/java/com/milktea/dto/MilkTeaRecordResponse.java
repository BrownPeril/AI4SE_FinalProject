package com.milktea.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MilkTeaRecordResponse {
    private Long id;
    private String brand;
    private String drinkName;
    private BigDecimal price;
    private LocalDateTime consumeDate;
    private String sugarLevel;
    private String iceLevel;
    private String cupSize;
    private String teaBase;
    private Integer rating;
    private String comment;
    private Boolean wouldRecommend;
    private LocalDateTime createdAt;
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
