package com.itaybs.expensemate.Service;

import static com.itaybs.expensemate.Utils.ParticipantUtils.findParticipantById;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itaybs.expensemate.Model.Expense;
import com.itaybs.expensemate.Model.Group;
import com.itaybs.expensemate.Model.Participant;
import com.itaybs.expensemate.Model.SettleUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupDataFetcher {

    public void fetchGroupData(String groupId, FetchGroupCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference groupRef = db.collection("groups").document(groupId);

        groupRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String id = document.getId();
                    String name = document.getString("name");

                    List<Map<String, Object>> participantsList = (List<Map<String, Object>>) document.get("participants");
                    ArrayList<Participant> participants = new ArrayList<>();
                    if (participantsList != null) {
                        for (Map<String, Object> participantMap : participantsList) {
                            String participantId = (String) participantMap.get("id");
                            String email = (String) participantMap.get("email");
                            Double balance = participantMap.get("balance") instanceof Long ? ((Long) participantMap.get("balance")).doubleValue() : (Double) participantMap.get("balance");
                            participants.add(new Participant(participantId, email, balance));
                        }
                    }

                    List<Map<String, Object>> expensesList = (List<Map<String, Object>>) document.get("expenses");
                    ArrayList<Expense> expenses = new ArrayList<>();
                    if (expensesList != null) {
                        for (Map<String, Object> expenseMap : expensesList) {
                            String expenseId = (String) expenseMap.get("id");
                            String description = (String) expenseMap.get("description");
                            Double amount = (Double) expenseMap.get("amount");
                            Timestamp createdAt = expenseMap.get("createdAt") instanceof Timestamp ? (Timestamp) expenseMap.get("createdAt") : Timestamp.now();
                            String participantId = (String) expenseMap.get("participant");
                            Participant expenseParticipant = findParticipantById(participants, participantId);
                            assert createdAt != null;
                            expenses.add(new Expense(expenseId, createdAt.toDate(), expenseParticipant, description, amount));
                        }
                    }

                    List<Map<String, Object>> settleUpsList = (List<Map<String, Object>>) document.get("settleUps");
                    ArrayList<SettleUp> settleUps = new ArrayList<>();
                    if (settleUpsList != null) {
                        for (Map<String, Object> settleUpMap : settleUpsList) {
                            String settleUpId = (String) settleUpMap.get("id");
                            Timestamp createdAt = settleUpMap.get("createdAt") instanceof Timestamp ? (Timestamp) settleUpMap.get("createdAt") : Timestamp.now();
                            String fromParticipantId = (String) settleUpMap.get("fromParticipant");
                            String toParticipantId = (String) settleUpMap.get("toParticipant");
                            Double amount = (Double) settleUpMap.get("amount");
                            Participant fromParticipant = findParticipantById(participants, fromParticipantId);
                            Participant toParticipant = findParticipantById(participants, toParticipantId);

                            assert createdAt != null;
                            settleUps.add(new SettleUp(settleUpId, createdAt.toDate(), fromParticipant, toParticipant, amount));
                        }
                    }

                    Group group = new Group(id, name, expenses, settleUps, participants);
                    callback.onSuccess(group);
                } else {
                    callback.onFailure(new Exception("No such document"));
                }
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    public interface FetchGroupCallback {
        void onSuccess(Group group);
        void onFailure(Exception e);
    }
}
