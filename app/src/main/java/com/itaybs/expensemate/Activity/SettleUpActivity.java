package com.itaybs.expensemate.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.itaybs.expensemate.Controller.GroupController;
import com.itaybs.expensemate.Interface.GenericCallback;
import com.itaybs.expensemate.Model.Participant;
import com.itaybs.expensemate.R;

import java.util.ArrayList;
import java.util.Objects;

public class SettleUpActivity extends AppCompatActivity {
    private EditText amountEditText;
    private GroupController groupController;
    private Spinner fromSpinner;
    private Spinner toSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_up);
        groupController = GroupController.getInstance();
        setupToolbar();
        setupForm();
    }

    private void setupToolbar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        topAppBar.setNavigationOnClickListener(e -> finish());
    }

    private void setupForm() {
        String fromParticipantId = getIntent().getStringExtra("FROM_PARTICIPANT_ID");
        String toParticipantId = getIntent().getStringExtra("TO_PARTICIPANT_ID");
        ArrayList<Participant> participants = getIntent().getParcelableArrayListExtra("PARTICIPANTS");

        int fromIndex = 0; // default will be the first
        int toIndex = 1; // default will be the second

        // Find the indices of fromParticipant and toParticipant in a single loop
        for (int i = 0; i < participants.size(); i++) {
            Participant currentParticipant = participants.get(i);
            if (Objects.equals(fromParticipantId, currentParticipant.getId())) {
                fromIndex = i;
            }
            if (Objects.equals(toParticipantId, currentParticipant.getId())) {
                toIndex = i;
            }
        }

        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);

        // Populate the spinners
        ArrayAdapter<Participant> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, participants);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        fromSpinner.setSelection(fromIndex);
        toSpinner.setAdapter(adapter);
        toSpinner.setSelection(toIndex);

        amountEditText = findViewById(R.id.amountEditText);
        MaterialButton saveButton = findViewById(R.id.addSettleUp);

        saveButton.setOnClickListener(v -> saveSettleUp());
    }

    private void saveSettleUp() {
        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please insert valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Please insert valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        Participant fromParticipant = (Participant) fromSpinner.getSelectedItem();
        Participant toParticipant = (Participant) toSpinner.getSelectedItem();
        if (Objects.equals(fromParticipant.getId(), toParticipant.getId())) {
            Toast.makeText(this, "You cannot settle yourself.", Toast.LENGTH_SHORT).show();
            return;
        }
        groupController.addSettleUp(fromParticipant, toParticipant, amount, new GenericCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(SettleUpActivity.this, "Settle up added!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure() {
                Toast.makeText(SettleUpActivity.this, "Failed to add settle up", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
