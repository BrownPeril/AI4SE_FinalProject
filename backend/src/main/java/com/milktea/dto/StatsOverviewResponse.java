package com.milktea.dto;

import java.math.BigDecimal;

public class StatsOverviewResponse {
    private BigDecimal totalSpent;
    private long totalCount;
    private BigDecimal avgPrice;
    private String topBrand;

    public StatsOverviewResponse() {}

    public StatsOverviewResponse(BigDecimal totalSpent, long totalCount, BigDecimal avgPrice, String topBrand) {
        this.totalSpent = totalSpent;
        this.totalCount = totalCount;
        this.avgPrice = avgPrice;
        this.topBrand = topBrand;
    }

    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    public BigDecimal getAvgPrice() { return avgPrice; }
    public void setAvgPrice(BigDecimal avgPrice) { this.avgPrice = avgPrice; }
    public String getTopBrand() { return topBrand; }
    public void setTopBrand(String topBrand) { this.topBrand = topBrand; }
}
