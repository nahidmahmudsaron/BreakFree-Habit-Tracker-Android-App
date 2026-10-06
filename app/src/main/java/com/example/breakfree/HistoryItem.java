package com.example.breakfree;

public class HistoryItem {

    private final long startDate;
    private final long endDate;
    private final int days;
    private final String rankName;

    public HistoryItem(long startDate, long endDate, int days, String rankName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
        this.rankName = rankName;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getDays() {
        return days;
    }

    public String getRankName() {
        return rankName;
    }
}
