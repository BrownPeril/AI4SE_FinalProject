package com.milktea.dto;

import java.math.BigDecimal;
import java.util.List;

public class BrandDistributionResponse {
    private List<String> brands;
    private List<Long> counts;
    private List<BigDecimal> amounts;

    public List<String> getBrands() { return brands; }
    public void setBrands(List<String> brands) { this.brands = brands; }
    public List<Long> getCounts() { return counts; }
    public void setCounts(List<Long> counts) { this.counts = counts; }
    public List<BigDecimal> getAmounts() { return amounts; }
    public void setAmounts(List<BigDecimal> amounts) { this.amounts = amounts; }
}
