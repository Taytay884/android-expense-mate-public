package com.itaybs.expensemate.Model;

public class GroupParticipantListItem {
    private String avatarText;
    private String mainText;
    private String rightText;
    private boolean showDeleteIcon;

    // Constructor
    public GroupParticipantListItem(String avatarText, String mainText, String rightText, boolean showDeleteIcon) {
        this.avatarText = avatarText;
        this.mainText = mainText;
        this.rightText = rightText;
        this.showDeleteIcon = showDeleteIcon;
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

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public boolean getShowDeleteIcon() { return showDeleteIcon; }

    public void setShowDeleteIcon(boolean showDeleteIcon) { this.showDeleteIcon = showDeleteIcon; };
}
