package com.itaybs.expensemate.Model;

import java.util.Date;

public class SettleUp {
    String id;
    Date createdAt;
    Participant fromParticipant;
    Participant toParticipant;
    double amount;

    // Constructor
    public SettleUp(String id, Date createdAt, Participant fromParticipant, Participant toParticipant, Double amount) {
        this.id = id;
        this.createdAt = createdAt;
        this.fromParticipant = fromParticipant;
        this.toParticipant = toParticipant;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Participant getFromParticipant() {
        return fromParticipant;
    }

    public void setFromParticipant(Participant fromParticipant) {
        this.fromParticipant = fromParticipant;
    }

    public Participant getToParticipant() {
        return toParticipant;
    }

    public void setToParticipant(Participant toParticipant) {
        this.toParticipant = toParticipant;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
