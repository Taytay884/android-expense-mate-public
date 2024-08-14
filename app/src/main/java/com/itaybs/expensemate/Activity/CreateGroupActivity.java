package com.itaybs.expensemate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.itaybs.expensemate.Adapter.GroupParticipantListItemAdapter;
import com.itaybs.expensemate.Model.GroupParticipantListItem;
import com.itaybs.expensemate.Model.Participant;
import com.itaybs.expensemate.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {
    private EditText emailTextView;
    private EditText groupNameTextView;
    private ProgressBar progressBar;
    private GroupParticipantListItemAdapter adapter;
    private List<GroupParticipantListItem> participantsList;
    private CollectionReference usersRef;
    private CollectionReference groupsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // Set click listener for the hamburger icon
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        topAppBar.setNavigationOnClickListener(e -> moveToGroupsActivity());

        emailTextView = findViewById(R.id.email);
        groupNameTextView = findViewById(R.id.groupName);
        MaterialButton addGroupParticipantBtn = findViewById(R.id.addGroupParticipant);
        progressBar = findViewById(R.id.progressBar);

        // Set on Click Listener on Add group participant button
        addGroupParticipantBtn.setOnClickListener(v -> addGroupParticipant());

        MaterialButton createGroupBtn = findViewById(R.id.createGroup);
        createGroupBtn.setOnClickListener(v -> createGroup());

        RecyclerView participantsView = findViewById(R.id.participants);
        participantsView.setLayoutManager(new LinearLayoutManager(this));
        participantsList = new ArrayList<>();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        participantsList.add(new GroupParticipantListItem("", currentUserEmail, "", false));

        adapter = new GroupParticipantListItemAdapter(participantsList);
        participantsView.setAdapter(adapter);

        // Initialize Firestore references
        usersRef = FirebaseFirestore.getInstance().collection("users");
        groupsRef = FirebaseFirestore.getInstance().collection("groups");
    }

    private void moveToGroupsActivity() {
        Intent intent = new Intent(CreateGroupActivity.this, GroupsActivity.class);
        startActivity(intent);
        finish();
    }

    private void addGroupParticipant() {
        String email = emailTextView.getText().toString();

        // Validations for input email
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            return;
        }

        participantsList.add(new GroupParticipantListItem("", email, "delete", true));
        adapter.notifyDataSetChanged();
        emailTextView.setText("");
    }

    private void createGroup() {
        String groupName = groupNameTextView.getText().toString().trim();

        if (TextUtils.isEmpty(groupName)) {
            Toast.makeText(getApplicationContext(), "Please enter group name!!", Toast.LENGTH_LONG).show();
            return;
        }
        if (participantsList.size() < 2) {
            Toast.makeText(getApplicationContext(), "You must add at least 1 participant", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        checkEmailsAndCreateGroup(groupName);
    }

    private void checkEmailsAndCreateGroup(String groupName) {
        List<String> emailsToCheck = new ArrayList<>();
        for (GroupParticipantListItem item : participantsList) {
            emailsToCheck.add(item.getMainText());
        }

        usersRef.whereIn("email", emailsToCheck).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<String> validEmails = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot) {
                        validEmails.add(document.getString("email"));
                    }

                    if (validEmails.size() == emailsToCheck.size()) {
                        createGroupInDatabase(groupName, validEmails);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CreateGroupActivity.this, "Some emails are invalid.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreateGroupActivity.this, "Failed to check emails.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createGroupInDatabase(String groupName, List<String> emails) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        String groupId = groupsRef.document().getId();

        Map<String, Object> groupData = new HashMap<>();
        groupData.put("name", groupName);
        ArrayList<Participant> participants = new ArrayList<>();
        emails.forEach(email -> {
            String participantId = FirebaseFirestore.getInstance().collection("participants").document().getId();
            participants.add(new Participant(participantId, email));
        });
        groupData.put("participants", participants);
        groupData.put("expenses", new ArrayList<>());
        groupData.put("settleUps", new ArrayList<>());

        batch.set(groupsRef.document(groupId), groupData);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(CreateGroupActivity.this, "Group created successfully.", Toast.LENGTH_LONG).show();
                    moveToGroupsActivity();
                } else {
                    Toast.makeText(CreateGroupActivity.this, "Failed to create group.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
