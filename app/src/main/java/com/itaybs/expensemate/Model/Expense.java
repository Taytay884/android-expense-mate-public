package com.itaybs.expensemate.Model;

import androidx.annotation.NonNull;

import java.util.Date;

public class Expense {
    private String id;
    private Participant participant;
    private String description;
    private Date createdAt;
    private Double amount;

    // Constructor
    public Expense(String id, Date createdAt, Participant participant, String description, Double amount) {
        this.id = id;
        this.createdAt = createdAt;
        this.participant = participant;
        this.description = description;
        this.amount = amount;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    @Override
    public String toString() {
        return participant.getEmail() + " - " + description + " - " + amount;
    }
}
