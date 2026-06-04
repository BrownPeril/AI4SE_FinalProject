package com.milktea.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MilkTeaRecordRequest {

    @NotBlank(message = "品牌不能为空")
    private String brand;

    @NotBlank(message = "饮品名不能为空")
    private String drinkName;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0", message = "价格不能为负数")
    private BigDecimal price;

    @NotNull(message = "消费时间不能为空")
    private LocalDateTime consumeDate;

    private String sugarLevel;
    private String iceLevel;
    private String cupSize;
    private String teaBase;

    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    private String comment;
    private Boolean wouldRecommend;

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
}
