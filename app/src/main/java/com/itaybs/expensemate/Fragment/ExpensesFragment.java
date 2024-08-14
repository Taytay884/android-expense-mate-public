package com.itaybs.expensemate.Fragment;

import static com.itaybs.expensemate.Utils.StringUtils.formatDollars;
import static com.itaybs.expensemate.Utils.StringUtils.getEmailAsName;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.itaybs.expensemate.Adapter.ExpenseListItemAdapter;
import com.itaybs.expensemate.Controller.GroupController;
import com.itaybs.expensemate.Interface.GenericCallback;
import com.itaybs.expensemate.Interface.OnDeleteListItemListener;
import com.itaybs.expensemate.Model.Expense;
import com.itaybs.expensemate.Model.ExpenseListItem;
import com.itaybs.expensemate.R;
import com.itaybs.expensemate.Utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpensesFragment extends Fragment {

    GroupController groupController;
    private View view;
    private RecyclerView expensesView;
    private ArrayList<Expense> expenses;
    private Double currentUserBalance;

    public void setGroupData(ArrayList<Expense> expenses, Double currentUserBalance) {
        this.expenses = expenses;
        this.currentUserBalance = currentUserBalance;
        updateExpensesRecyclerView();
        updateCurrentParticipantBalance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_expenses, container, false);
        groupController = GroupController.getInstance();
        setupView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        groupController.fetchGroupData();
    }

    private void setupView() {
        setupButtons();
        setupExpensesView();
    }

    private void setupButtons() {
        MaterialButton addExpenseButton = view.findViewById(R.id.addExpenseButton);
        addExpenseButton.setOnClickListener(v -> groupController.openAddExpenseActivity());
        MaterialButton settleUpButton = view.findViewById(R.id.settleUpButton);
        settleUpButton.setOnClickListener(v -> groupController.openSettleUpActivity());
    }

    private void setupExpensesView() {
        expensesView = view.findViewById(R.id.expensesRecyclerView);
        expensesView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void updateExpensesRecyclerView() {
        List<ExpenseListItem> expensesList = createExpenseListItems();
        MaterialTextView emptyMessageTextView = view.findViewById(R.id.emptyMessageTextView);
        emptyMessageTextView.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);

        ExpenseListItemAdapter expensesAdapter = new ExpenseListItemAdapter(expensesList, new OnDeleteListItemListener() {
            @Override
            public void onDeleteClick(int position, String id) {
                groupController.handleDeleteExpense(id, new GenericCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(), "Expense deleted", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(requireContext(), "Failed to delete expense", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        expensesView.setAdapter(expensesAdapter);
    }

    private List<ExpenseListItem> createExpenseListItems() {
        List<ExpenseListItem> expensesList = new ArrayList<>();
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        for (Expense expense : expenses) {
            boolean isReceive = Objects.equals(currentUserEmail, expense.getParticipant().getEmail());
            String formattedDate = DateUtils.formatDate(expense.getCreatedAt());
            String createdBy = isReceive ? "you" : getEmailAsName(expense.getParticipant().getEmail());
            expensesList.add(new ExpenseListItem(
                    requireContext(),
                    expense.getId(),
                    expense.getDescription(),
                    formattedDate + ", by " + createdBy,
                    formatDollars(expense.getAmount()),
                    isReceive));
        }
        return expensesList;
    }

    private void updateCurrentParticipantBalance() {
        TextView balanceAmount = view.findViewById(R.id.balanceAmount);
        balanceAmount.setText(formatDollars(currentUserBalance));
    }

}