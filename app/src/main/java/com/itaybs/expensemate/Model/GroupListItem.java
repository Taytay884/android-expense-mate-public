package com.itaybs.expensemate.Model;

public class GroupListItem {
    private String id;
    private String avatarText;
    private String mainText;
    private String subtext;
    private String rightText;

    // Constructor
    public GroupListItem(String id, String mainText, String subtext, String rightText) {
        this.id = id;
        this.mainText = mainText;
        this.subtext = subtext;
        this.rightText = rightText;
    }

    // Getters and setters
    public String getAvatarText() {
        return avatarText;
    }

    public void setAvatarText(String avatarText) {
        this.avatarText = avatarText;
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
