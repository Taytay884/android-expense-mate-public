package com.itaybs.expensemate.Model;

import android.os.Parcelable;

import java.util.ArrayList;

public class Group {
    private String id;
    private String name;
    private ArrayList<Expense> expenses;
    private ArrayList<SettleUp> settleUps;
    private ArrayList<Participant> participants;

    // Get from DB
    public Group(String id, String name, ArrayList<Expense> expenses, ArrayList<SettleUp> settleUps, ArrayList<Participant> participants) {
        this.id = id;
        this.name = name;
        this.expenses = expenses;
        this.settleUps = settleUps;
        this.participants = participants;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }

    public ArrayList<Participant> getParticipants() {
        return (ArrayList<Participant>) participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public ArrayList<SettleUp> getSettleUps() {
        return settleUps;
    }

    public void setSettleUps(ArrayList<SettleUp> settleUps) {
        this.settleUps = settleUps;
    }
}
