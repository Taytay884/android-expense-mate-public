package com.itaybs.expensemate.Activity;

import static com.itaybs.expensemate.Utils.ParticipantUtils.findParticipantByEmail;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.itaybs.expensemate.Controller.GroupController;
import com.itaybs.expensemate.Fragment.BalancesFragment;
import com.itaybs.expensemate.Fragment.ExpensesFragment;
import com.itaybs.expensemate.Model.Group;
import com.itaybs.expensemate.Model.Participant;
import com.itaybs.expensemate.R;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Objects;

public class GroupActivity extends AppCompatActivity {
    private static GroupController groupController;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ExpensesFragment expensesFragment;
    private BalancesFragment balancesFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        setupGroupController();
        initializeUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        groupController.destroy();
    }

    private void setupGroupController() {
        // Retrieve the GROUP_ID from the Intent
        Intent intent = getIntent();
        if (intent.hasExtra("GROUP_ID")) {
            String groupId = intent.getStringExtra("GROUP_ID");
            groupController = GroupController.getInstance(this, groupId);
            groupController.fetchGroupData();
        }
    }

    private void initializeUI() {
        setupToolbar();
        setupViewPager();
        setupTabs();
    }

    private void setupToolbar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        topAppBar.setNavigationOnClickListener(e -> moveToGroupsActivity());

        // Retrieve GROUP_NAME from Intent
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("GROUP_NAME");
        topAppBar.setTitle(groupName != null ? groupName : "Group name");
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GroupFragmentStateAdapter(this));
    }

    private void setupTabs() {
        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Expenses");
                    break;
                case 1:
                    tab.setText("Balances");
                    break;
            }
        }).attach();
    }

    private void moveToGroupsActivity() {
        Intent intent = new Intent(GroupActivity.this, GroupsActivity.class);
        startActivity(intent);
        finish();
    }

    private class GroupFragmentStateAdapter extends FragmentStateAdapter {

        public GroupFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    expensesFragment = new ExpensesFragment();
                    return expensesFragment;
                case 1:
                    balancesFragment = new BalancesFragment();
                    return balancesFragment;
                default:
                    return new ExpensesFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    private Participant getCurrentParticipant(Group group) {
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        return findParticipantByEmail(group.getParticipants(), currentUserEmail);
    }

    public void updateUI(Group groupFromDatabase) {
        if (tabLayout.getSelectedTabPosition() == 0) {
            expensesFragment.setGroupData(groupFromDatabase.getExpenses(), getCurrentParticipant(groupFromDatabase).getBalance());
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            balancesFragment.setGroupData(groupFromDatabase.getParticipants(), getCurrentParticipant(groupFromDatabase), groupFromDatabase.getSettleUps());
        }
    }
}
