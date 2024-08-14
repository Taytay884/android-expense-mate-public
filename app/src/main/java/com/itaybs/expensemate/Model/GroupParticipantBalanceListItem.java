package com.itaybs.expensemate.Model;

public class GroupParticipantBalanceListItem {
    private String id;
    private String mainText;
    private String subtext;
    private String rightText;
    private boolean showSettleButton;

    // Constructor
    public GroupParticipantBalanceListItem(String id, String mainText, String subtext, String rightText, boolean showSettleButton) {
        this.id = id;
        this.mainText = mainText;
        this.subtext = subtext;
        this.showSettleButton = showSettleButton;
        this.rightText = rightText;
    }


    // Getters and setters
    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public boolean isShowSettleButton() {
        return showSettleButton;
    }

    public void setShowSettleButton(boolean showSettleButton) {
        this.showSettleButton = showSettleButton;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}