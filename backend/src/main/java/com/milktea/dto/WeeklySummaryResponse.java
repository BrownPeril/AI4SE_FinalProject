package com.milktea.dto;

import java.math.BigDecimal;
import java.util.List;

public class WeeklySummaryResponse {
    private List<String> weeks;
    private List<BigDecimal> amounts;
    private List<Long> counts;

    public List<String> getWeeks() { return weeks; }
    public void setWeeks(List<String> weeks) { this.weeks = weeks; }
    public List<BigDecimal> getAmounts() { return amounts; }
    public void setAmounts(List<BigDecimal> amounts) { this.amounts = amounts; }
    public List<Long> getCounts() { return counts; }
    public void setCounts(List<Long> counts) { this.counts = counts; }
}
