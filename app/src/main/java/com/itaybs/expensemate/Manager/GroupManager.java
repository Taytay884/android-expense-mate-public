package com.itaybs.expensemate.Manager;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.itaybs.expensemate.Interface.GenericCallback;
import com.itaybs.expensemate.Model.Expense;
import com.itaybs.expensemate.Model.Group;
import com.itaybs.expensemate.Model.Participant;
import com.itaybs.expensemate.Model.SettleUp;
import com.itaybs.expensemate.Service.GroupDataFetcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupManager {
    private static final String TAG = "GroupManager";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String groupId;
    private Group group;

    public GroupManager(String groupId) {
        this.groupId = groupId;
    }

    public void fetchGroupData(GroupDataFetcher.FetchGroupCallback callback) {
        new GroupDataFetcher().fetchGroupData(groupId, callback);
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void addExpense(Expense expense, GenericCallback callback) {
        saveExpenseToFirestore(expense, callback);
    }

    private void saveExpenseToFirestore(Expense expense, GenericCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // Reference to the group document
        DocumentReference groupRef = db.collection("groups").document(groupId);


        groupRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> participants = (List<Map<String, Object>>) documentSnapshot.get("participants");
                int numberOfParticipants = participants.size();

                // Calculate the expense share for each participant
                double participantExpense = expense.getAmount() / numberOfParticipants;

                for (Map<String, Object> participant : participants) {
                    double currentBalance = (double) participant.get("balance");
                    String participantId = (String) participant.get("id");

                    double newBalance;
                    if (participantId.equals(expense.getParticipant().getId())) {
                        // Update balance for the participant who added the expense
                        newBalance = currentBalance + (expense.getAmount() - participantExpense);
                    } else {
                        // Update balance for the other participants
                        newBalance = currentBalance - participantExpense;
                    }
                    participant.put("balance", newBalance);
                }

                // Create a map for the expense data
                Map<String, Object> expenseData = new HashMap<>();
                expenseData.put("id", expense.getId());
                expenseData.put("createdAt", expense.getCreatedAt());
                expenseData.put("participant", expense.getParticipant().getId()); // Store participant ID
                expenseData.put("description", expense.getDescription());
                expenseData.put("amount", expense.getAmount());

                // Update the participants array and add the expense to the group's expenses array
                batch.update(groupRef, "participants", participants);
                batch.update(groupRef, "expenses", FieldValue.arrayUnion(expenseData));

                // Commit the batch
                batch.commit().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            callback.onFailure();
        });
    }

    public void deleteExpense(Expense expenseToDelete, GenericCallback callback) {
        if (group == null) return;

        updateBalancesForExpenseDeletion(expenseToDelete);
        deleteExpenseFromFirestore(expenseToDelete, callback);
    }

    public void deleteSettleUp(SettleUp settleUpToDelete, GenericCallback callback) {
        if (group == null) return;

        updateBalancesForSettleUpDeletion(settleUpToDelete);
        deleteSettleUpFromFirestore(settleUpToDelete, callback);
    }

    private void updateBalancesForSettleUpDeletion(SettleUp settleUp) {
        ArrayList<Participant> participants = group.getParticipants();
        for (Participant participant : participants) {
            if (participant.getId().equals(settleUp.getToParticipant().getId())) {
                // Update balance for the participant who sends
                participant.setBalance(participant.getBalance() + settleUp.getAmount());
            }
            if (participant.getId().equals(settleUp.getFromParticipant().getId())) {
                // Update balance for the participant who receives
                participant.setBalance(participant.getBalance() - settleUp.getAmount());
            }
        }
    }

    private void deleteSettleUpFromFirestore(SettleUp settleUpToDelete, GenericCallback callback) {
        DocumentReference groupRef = db.collection("groups").document(groupId);
        WriteBatch batch = db.batch();
        batch.update(groupRef, "participants", group.getParticipants());
        batch.update(groupRef, "settleUps", FieldValue.arrayRemove(createSettleUpMap(settleUpToDelete)));

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Settle up deleted successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error while deleting settle up: ", e);
                    callback.onFailure();
                });
    }

    public void addSettleUp(Participant fromParticipant, Participant toParticipant, double amount, GenericCallback callback) {
        DocumentReference groupRef = FirebaseFirestore.getInstance().collection("groups").document(groupId);
        DocumentReference newSettleUpRef = groupRef.collection("settleUps").document(); // Generates a new unique ID
        SettleUp settleUpToAdd = new SettleUp(newSettleUpRef.getId(), new Date(), fromParticipant, toParticipant, amount);
        saveSettleUpToFirebase(settleUpToAdd, callback);
    }

    private void saveSettleUpToFirebase(SettleUp settleUp, GenericCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // Reference to the group document
        DocumentReference groupRef = db.collection("groups").document(groupId);


        groupRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> participants = (List<Map<String, Object>>) documentSnapshot.get("participants");
                for (Map<String, Object> participant : participants) {
                    String participantId = (String) participant.get("id");
                    double currentBalance = (double) participant.get("balance");

                    double newBalance = currentBalance;
                    assert participantId != null;
                    if (participantId.equals(settleUp.getFromParticipant().getId())) {
                        // Update balance for the participant who sends
                        newBalance = currentBalance + settleUp.getAmount();
                    }
                    if (participantId.equals(settleUp.getToParticipant().getId())) {
                        // Update balance for the participant who receives
                        newBalance = currentBalance - settleUp.getAmount();
                    }
                    participant.put("balance", newBalance);
                }

                // Create a map for the expense data
                Map<String, Object> settleUpData = new HashMap<>();
                settleUpData.put("id", settleUp.getId());
                settleUpData.put("createdAt", settleUp.getCreatedAt());
                settleUpData.put("fromParticipant", settleUp.getFromParticipant().getId());
                settleUpData.put("toParticipant", settleUp.getToParticipant().getId());
                settleUpData.put("amount", settleUp.getAmount());

                // Update the participants array and add the expense to the group's expenses array
                batch.update(groupRef, "participants", participants);
                batch.update(groupRef, "settleUps", FieldValue.arrayUnion(settleUpData));

                // Commit the batch
                batch.commit().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            callback.onFailure();
        });
    }

    private void updateBalancesForExpenseDeletion(Expense expenseToDelete) {
        ArrayList<Participant> participants = group.getParticipants();
        double participantExpense = expenseToDelete.getAmount() / participants.size();
        for (Participant participant : participants) {
            double newBalance = participant.getId().equals(expenseToDelete.getParticipant().getId())
                    ? participant.getBalance() - (expenseToDelete.getAmount() - participantExpense)
                    : participant.getBalance() + participantExpense;
            participant.setBalance(newBalance);
        }
    }

    private void deleteExpenseFromFirestore(Expense expenseToDelete, GenericCallback callback) {
        DocumentReference groupRef = db.collection("groups").document(groupId);
        WriteBatch batch = db.batch();
        batch.update(groupRef, "participants", group.getParticipants());
        batch.update(groupRef, "expenses", FieldValue.arrayRemove(createExpenseMap(expenseToDelete)));

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Expense deleted successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting expense: ", e);
                    callback.onFailure();
                });
    }

    private Map<String, Object> createExpenseMap(Expense expense) {
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("id", expense.getId());
        expenseData.put("createdAt", expense.getCreatedAt());
        expenseData.put("participant", expense.getParticipant().getId());
        expenseData.put("description", expense.getDescription());
        expenseData.put("amount", expense.getAmount());
        return expenseData;
    }

    private Map<String, Object> createSettleUpMap(SettleUp settleUp) {
        Map<String, Object> settleUpData = new HashMap<>();
        settleUpData.put("id", settleUp.getId());
        settleUpData.put("createdAt", settleUp.getCreatedAt());
        settleUpData.put("fromParticipant", settleUp.getFromParticipant().getId());
        settleUpData.put("toParticipant", settleUp.getToParticipant().getId());
        settleUpData.put("amount", settleUp.getAmount());
        return settleUpData;
    }
}
