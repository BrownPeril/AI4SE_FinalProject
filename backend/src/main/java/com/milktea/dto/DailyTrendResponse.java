package com.milktea.dto;

import java.math.BigDecimal;
import java.util.List;

public class DailyTrendResponse {
    private List<String> dates;
    private List<BigDecimal> amounts;

    public List<String> getDates() { return dates; }
    public void setDates(List<String> dates) { this.dates = dates; }
    public List<BigDecimal> getAmounts() { return amounts; }
    public void setAmounts(List<BigDecimal> amounts) { this.amounts = amounts; }
}
