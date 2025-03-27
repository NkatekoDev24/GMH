package com.example.gmh_app.Classes;

public class BeforeVideo9Response {
    private String moneyInflows;
    private String goodHabits;
    private String easyAdjustment;
    private String changes;
    private String outcome;
    private String satisfaction;
    private String businessLocation;

    public BeforeVideo9Response() {
        // Default constructor for Firebase
    }

    public BeforeVideo9Response(String moneyInflows, String goodHabits, String easyAdjustment, String changes, String outcome, String satisfaction, String businessLocation) {
        this.moneyInflows = moneyInflows;
        this.goodHabits = goodHabits;
        this.easyAdjustment = easyAdjustment;
        this.changes = changes;
        this.outcome = outcome;
        this.satisfaction = satisfaction;
        this.businessLocation = businessLocation;
    }

    public String getMoneyInflows() {
        return moneyInflows;
    }

    public String getGoodHabits() {
        return goodHabits;
    }

    public String getEasyAdjustment() {
        return easyAdjustment;
    }

    public String getChanges() {
        return changes;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getSatisfaction() {
        return satisfaction;
    }

    public String getBusinessLocation() {
        return businessLocation;
    }
}
