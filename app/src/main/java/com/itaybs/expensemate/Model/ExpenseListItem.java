package com.itaybs.expensemate.Model;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.itaybs.expensemate.R;

public class ExpenseListItem {
    private String id;
    private int avatarImageResource;

    public int getAvatarColor() {
        return avatarColor;
    }

    public void setAvatarColor(int avatarColor) {
        this.avatarColor = avatarColor;
    }

    private int avatarColor;
    private String mainText;
    private String subtext;
    private String rightText;

    // Constructor
    public ExpenseListItem(Context context, String id, String mainText, String subtext, String rightText, boolean isReceive) {
        this.id = id;
        this.mainText = mainText;
        this.subtext = subtext;
        this.rightText = rightText;
        this.avatarImageResource = isReceive ? R.drawable.trending_up : R.drawable.trending_down;
        this.avatarColor = isReceive ? ContextCompat.getColor(context, R.color.colorSuccess) : ContextCompat.getColor(context, R.color.colorFailure);
    }

    // Getters and setters
    public int getAvatarImageResource() {
        return avatarImageResource;
    }

    public void setAvatarImageResource(int avatarImageResource) {
        this.avatarImageResource = avatarImageResource;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}