package com.example.gmh_app.Classes;

public class BeforeVideo11Response {
    private String rent;
    private String own;
    private String free;
    private String otherArrangements;
    private String structure;
    private String location;
    private String moveReason;

    public BeforeVideo11Response() {
        // Default constructor required for Firebase
    }

    public BeforeVideo11Response(String rent, String own, String free, String otherArrangements, String structure, String location, String moveReason) {
        this.rent = rent;
        this.own = own;
        this.free = free;
        this.otherArrangements = otherArrangements;
        this.structure = structure;
        this.location = location;
        this.moveReason = moveReason;
    }

    public String getRent() {
        return rent;
    }

    public String getOwn() {
        return own;
    }

    public String getFree() {
        return free;
    }

    public String getOtherArrangements() {
        return otherArrangements;
    }

    public String getStructure() {
        return structure;
    }

    public String getLocation() {
        return location;
    }

    public String getMoveReason() {
        return moveReason;
    }
}
