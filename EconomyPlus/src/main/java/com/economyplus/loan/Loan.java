package com.economyplus.loan;

public class Loan {
    private final double principal;
    private double remaining;
    private long dueTime;
    private final double interestRate;

    public Loan(double principal, double remaining, long dueTime, double interestRate) {
        this.principal = principal;
        this.remaining = remaining;
        this.dueTime = dueTime;
        this.interestRate = interestRate;
    }

    public double getPrincipal() { return principal; }
    public double getRemaining() { return remaining; }
    public void setRemaining(double remaining) { this.remaining = remaining; }
    public long getDueTime() { return dueTime; }
    public void setDueTime(long dueTime) { this.dueTime = dueTime; }
    public double getInterestRate() { return interestRate; }

    public boolean isOverdue() { return System.currentTimeMillis() > dueTime; }

    public String getTimeLeft() {
        long diff = dueTime - System.currentTimeMillis();
        if (diff <= 0) return "OVERDUE";
        long days = diff / 86400000L;
        long hours = (diff % 86400000L) / 3600000L;
        return days + "d " + hours + "h";
    }
}
