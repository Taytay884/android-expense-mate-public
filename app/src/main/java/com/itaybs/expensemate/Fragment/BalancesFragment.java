package com.itaybs.expensemate.Fragment;

import static com.itaybs.expensemate.Utils.ParticipantUtils.findParticipantById;
import static com.itaybs.expensemate.Utils.StringUtils.formatDollars;
import static com.itaybs.expensemate.Utils.StringUtils.getEmailAsName;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.itaybs.expensemate.Adapter.GroupParticipantBalanceListItemAdapter;
import com.itaybs.expensemate.Adapter.SettleUpListItemAdapter;
import com.itaybs.expensemate.Controller.GroupController;
import com.itaybs.expensemate.Interface.GenericCallback;
import com.itaybs.expensemate.Interface.OnDeleteListItemListener;
import com.itaybs.expensemate.Interface.OnSettleClickListener;
import com.itaybs.expensemate.Model.GroupParticipantBalanceListItem;
import com.itaybs.expensemate.Model.Participant;
import com.itaybs.expensemate.Model.SettleUp;
import com.itaybs.expensemate.Model.SettleUpListItem;
import com.itaybs.expensemate.R;
import com.itaybs.expensemate.Utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BalancesFragment extends Fragment {

    private GroupController groupController;
    private ArrayList<Participant> participants;
    private Participant currentParticipant;
    private View view;
    private RecyclerView participantsView;
    private RecyclerView settleUpsView;
    private ArrayList<SettleUp> settleUps;

    public void setGroupData(ArrayList<Participant> participants, Participant currentParticipant, ArrayList<SettleUp> settleUps) {
        this.participants = participants;
        this.currentParticipant = currentParticipant;
        this.settleUps = settleUps;
        updateParticipantsRecyclerView();
        updateSettleUpsRecyclerView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balances, container, false);
        groupController = GroupController.getInstance();
        this.view = view;
        setupView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        groupController.fetchGroupData();
    }

    private void setupView() {
        setupParticipantsView();
        setupSettleUpsView();
    }

    private void setupParticipantsView() {
        participantsView = view.findViewById(R.id.participantsRecyclerView);
        participantsView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void updateParticipantsRecyclerView() {
        List<GroupParticipantBalanceListItem> participantBalances = createParticipantBalanceListItems();

        GroupParticipantBalanceListItemAdapter participantBalanceListItemAdapter =
                new GroupParticipantBalanceListItemAdapter(participantBalances, new OnSettleClickListener() {
            @Override
            public void onClick(int position, String participantId) {
                Participant clickedParticipant = findParticipantById(participants, participantId);
                groupController.openSettleUpActivity(currentParticipant, clickedParticipant);
            }
        });
        participantsView.setAdapter(participantBalanceListItemAdapter);
    }

    private List<GroupParticipantBalanceListItem> createParticipantBalanceListItems() {
        List<GroupParticipantBalanceListItem> participantsList = new ArrayList<>();
        String currentUserEmail = currentParticipant.getEmail();
        for (Participant participant : participants) {
            boolean isCurrentUser = Objects.equals(currentUserEmail, participant.getEmail());
            boolean isSettled = Math.abs(participant.getBalance()) < 0.009;
            participantsList.add(new GroupParticipantBalanceListItem(
                    participant.getId(),
                    participant.getEmail(),
                    formatDollars(participant.getBalance()),
                    isCurrentUser ? "This is you" : "Settled!",
                    !isSettled && !isCurrentUser
            ));
        }
        return participantsList;
    }

    private void setupSettleUpsView() {
        settleUpsView = view.findViewById(R.id.settleUpsRecyclerView);
        settleUpsView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void updateSettleUpsRecyclerView() {
        List<SettleUpListItem> settleUpListItemList = createSettleUpListItemList();
        MaterialTextView emptyMessageTextView = view.findViewById(R.id.emptyMessageTextView);
        emptyMessageTextView.setVisibility(settleUps.isEmpty() ? View.VISIBLE : View.GONE);

        SettleUpListItemAdapter expensesAdapter = new SettleUpListItemAdapter(settleUpListItemList, new OnDeleteListItemListener() {
            @Override
            public void onDeleteClick(int position, String id) {
                groupController.handleDeleteSettleUp(id, new GenericCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(), "Settle up deleted", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(requireContext(), "Failed to delete settle up", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        settleUpsView.setAdapter(expensesAdapter);
    }

    private List<SettleUpListItem> createSettleUpListItemList() {
        List<SettleUpListItem> settleUpsList = new ArrayList<>();
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        for (SettleUp settleUp : settleUps) {
            boolean isCurrentUserReceive = Objects.equals(currentUserEmail, settleUp.getToParticipant().getEmail());
            boolean isCurrentUserSent = Objects.equals(currentUserEmail, settleUp.getFromParticipant().getEmail());
            String formattedDate = DateUtils.formatDate(settleUp.getCreatedAt());
            String receiverName = isCurrentUserReceive ? "you" : getEmailAsName(settleUp.getToParticipant().getEmail());
            String senderName = isCurrentUserSent ? "you" : getEmailAsName(settleUp.getFromParticipant().getEmail());
            String description = senderName + " to " + receiverName;
            settleUpsList.add(new SettleUpListItem(
                    requireContext(),
                    settleUp.getId(),
                    description,
                    formattedDate,
                    formatDollars(settleUp.getAmount()),
                    isCurrentUserReceive,
                    isCurrentUserSent));
        }
        return settleUpsList;
    }
}
