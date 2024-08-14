package com.itaybs.expensemate.Controller;

import android.content.Intent;
import android.util.Log;

import com.itaybs.expensemate.Activity.AddExpenseActivity;
import com.itaybs.expensemate.Activity.GroupActivity;
import com.itaybs.expensemate.Activity.SettleUpActivity;
import com.itaybs.expensemate.Interface.GenericCallback;
import com.itaybs.expensemate.Manager.GroupManager;
import com.itaybs.expensemate.Model.Expense;
import com.itaybs.expensemate.Model.Group;
import com.itaybs.expensemate.Model.Participant;
import com.itaybs.expensemate.Model.SettleUp;
import com.itaybs.expensemate.Service.GroupDataFetcher;

// Singleton
public class GroupController {
    private static GroupController instance;

    // Private constructor to prevent instantiation
    private GroupController(GroupActivity activity, String groupId) {
        this.activity = activity;
        this.groupManager = new GroupManager(groupId);
    }

    private static final String TAG = "GroupController";
    private final GroupActivity activity;
    private final GroupManager groupManager;

    public static GroupController getInstance(GroupActivity activity, String groupId) {
        if (instance == null) {
            instance = new GroupController(activity, groupId);
        }
        return instance;
    }

    public static GroupController getInstance() {
        return instance;
    }

    public void fetchGroupData() {
        groupManager.fetchGroupData(new GroupDataFetcher.FetchGroupCallback() {
            @Override
            public void onSuccess(Group groupFromDatabase) {
                groupManager.setGroup(groupFromDatabase);
                activity.updateUI(groupFromDatabase);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error fetching group data: ", e);
            }
        });
    }

    public void handleDeleteExpense(String expenseId, GenericCallback callback) {
        Expense expenseToDelete = findExpenseById(expenseId);
        if (expenseToDelete == null) {
            callback.onFailure();
            return;
        }
        groupManager.deleteExpense(expenseToDelete, new GenericCallback() {
            @Override
            public void onSuccess() {
                fetchGroupData();
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public void handleDeleteSettleUp(String settleUpId, GenericCallback callback) {
        SettleUp settleUpToDelete = findSettleUpById(settleUpId);
        if (settleUpToDelete == null) {
            callback.onFailure();
            return;
        }
        groupManager.deleteSettleUp(settleUpToDelete, new GenericCallback() {
            @Override
            public void onSuccess() {
                fetchGroupData();
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    private Expense findExpenseById(String expenseId) {
        return groupManager.getGroup().getExpenses().stream()
                .filter(expense -> expense.getId().equals(expenseId))
                .findFirst()
                .orElse(null);
    }

    private SettleUp findSettleUpById(String settleUpId) {
        return groupManager.getGroup().getSettleUps().stream()
                .filter(settleUp -> settleUp.getId().equals(settleUpId))
                .findFirst()
                .orElse(null);
    }

    public void openAddExpenseActivity() {
        Intent intent = new Intent(activity, AddExpenseActivity.class);
        intent.putExtra("GROUP_ID", groupManager.getGroup().getId());
        intent.putParcelableArrayListExtra("PARTICIPANTS", groupManager.getGroup().getParticipants());
        activity.startActivity(intent);
    }

    public void openSettleUpActivity(Participant currentUserParticipant, Participant otherParticipant) {
        Intent intent = new Intent(activity, SettleUpActivity.class);
        boolean isFromCurrentUser = currentUserParticipant.getBalance() < otherParticipant.getBalance();
        Participant fromParticipant = isFromCurrentUser ? currentUserParticipant : otherParticipant;
        intent.putExtra("FROM_PARTICIPANT_ID", fromParticipant.getId());
        Participant toParticipant = !isFromCurrentUser ? currentUserParticipant : otherParticipant;
        intent.putExtra("TO_PARTICIPANT_ID", toParticipant.getId());
        intent.putParcelableArrayListExtra("PARTICIPANTS", groupManager.getGroup().getParticipants());
        activity.startActivity(intent);
    }

    public void openSettleUpActivity() {
        Intent intent = new Intent(activity, SettleUpActivity.class);
        intent.putParcelableArrayListExtra("PARTICIPANTS", groupManager.getGroup().getParticipants());
        activity.startActivity(intent);
    }

    public void addExpense(Expense expense, GenericCallback callback) {
        groupManager.addExpense(expense, new GenericCallback() {
            @Override
            public void onSuccess() {
                fetchGroupData();
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public void addSettleUp(Participant fromParticipant, Participant toParticipant, double amount, GenericCallback callback) {
        groupManager.addSettleUp(fromParticipant, toParticipant, amount, new GenericCallback() {
            @Override
            public void onSuccess() {
                fetchGroupData();
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public void destroy() {
        instance = null;
    }
}
