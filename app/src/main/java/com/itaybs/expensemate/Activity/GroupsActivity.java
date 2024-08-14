package com.itaybs.expensemate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itaybs.expensemate.Adapter.GroupListItemAdapter;
import com.itaybs.expensemate.Model.GroupListItem;
import com.itaybs.expensemate.R;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    private static final String TAG = "GroupsActivity";

    private DrawerLayout drawerLayout;
    private GroupListItemAdapter adapter;
    private List<GroupListItem> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Set click listener for the hamburger icon
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getItemId() == R.id.menu) {
                drawerLayout.openDrawer(GravityCompat.END);
                return true;
            }
            return false;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Handle navigation view item clicks here
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(GroupsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        MaterialButton createGroupBtn = findViewById(R.id.createGroup);
        createGroupBtn.setOnClickListener(v -> moveToCreateGroupActivity());

        RecyclerView groupsView = findViewById(R.id.groups);
        groupsView.setLayoutManager(new LinearLayoutManager(this));
        groupList = new ArrayList<>();

        // Pass the click listener to the adapter
        adapter = new GroupListItemAdapter(groupList, item -> {
            Intent intent = new Intent(GroupsActivity.this, GroupActivity.class);
            intent.putExtra("GROUP_ID", item.getId()); // Pass the group ID
            intent.putExtra("GROUP_NAME", item.getMainText()); // Pass the group ID
            startActivity(intent);
        });
        groupsView.setAdapter(adapter);

        fetchGroupsFromFirestore(); // Fetch the groups from Firestore
    }

    private void moveToCreateGroupActivity() {
        Intent intent = new Intent(GroupsActivity.this, CreateGroupActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchGroupsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            groupList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String name = document.getString("name");
                                String lastActivity = document.getString("lastActivity");
                                String balance = document.getString("balance");

                                GroupListItem group = new GroupListItem(id, name, lastActivity, balance);
                                groupList.add(group);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
