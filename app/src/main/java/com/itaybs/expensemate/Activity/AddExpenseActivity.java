package com.itaybs.expensemate.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itaybs.expensemate.Controller.GroupController;
import com.itaybs.expensemate.Interface.GenericCallback;
import com.itaybs.expensemate.Model.Expense;
import com.itaybs.expensemate.Model.Participant;
import com.itaybs.expensemate.R;

import java.util.ArrayList;
import java.util.Date;

public class AddExpenseActivity extends AppCompatActivity {
    private Spinner payerSpinner;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private String groupId;
    private GroupController groupController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        groupController = GroupController.getInstance();

        setupToolbar();

        // Retrieve participants and group ID from intent
        ArrayList<Participant> participants = getIntent().getParcelableArrayListExtra("PARTICIPANTS");
        groupId = getIntent().getStringExtra("GROUP_ID");

        payerSpinner = findViewById(R.id.payerSpinner);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        amountEditText = findViewById(R.id.amountEditText);
        Button saveButton = findViewById(R.id.saveButton);

        // Populate the payee spinner
        ArrayAdapter<Participant> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, participants);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payerSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> saveExpense());
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

    private void saveExpense() {
        Participant payee = (Participant) payerSpinner.getSelectedItem();
        String description = descriptionEditText.getText().toString();
        String amountStr = amountEditText.getText().toString();

        if (description.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // Reference to the group's expenses sub-collection
        DocumentReference groupRef = FirebaseFirestore.getInstance().collection("groups").document(groupId);
        DocumentReference newExpenseRef = groupRef.collection("expenses").document(); // Generates a new unique ID

        String expenseId = newExpenseRef.getId(); // Get the ID of the newly created expense document

        // Create the new expense with the generated ID
        Expense expense = new Expense(expenseId, new Date(), payee, description, amount);

        // Save the expense to Firestore
        groupController.addExpense(expense, new GenericCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddExpenseActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                finish();
            }

            public void onFailure() {
                Toast.makeText(AddExpenseActivity.this, "Failed to add expense", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
