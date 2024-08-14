package com.itaybs.expensemate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.itaybs.expensemate.Interface.OnSettleClickListener;
import com.itaybs.expensemate.Model.GroupParticipantBalanceListItem;
import com.itaybs.expensemate.R;

import java.util.List;

public class GroupParticipantBalanceListItemAdapter extends RecyclerView.Adapter<GroupParticipantBalanceListItemAdapter.ViewHolder> {

    private OnSettleClickListener settleButtonListener;
    private List<GroupParticipantBalanceListItem> itemList;

    public GroupParticipantBalanceListItemAdapter(List<GroupParticipantBalanceListItem> itemList, OnSettleClickListener settleButtonListener) {
        this.itemList = itemList;
        this.settleButtonListener = settleButtonListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group_participant_balance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        GroupParticipantBalanceListItem item = itemList.get(position);
        holder.mainText.setText(item.getMainText());
        holder.subText.setText(item.getSubtext());
        holder.rightText.setText(item.getRightText());

        holder.settleButton.setVisibility(item.isShowSettleButton() ? View.VISIBLE : View.GONE);
        holder.rightText.setVisibility(item.isShowSettleButton() ? View.GONE : View.VISIBLE);

        // Show/hide border based on position
        if (position == itemList.size() - 1) {
            holder.borderLayout.setVisibility(View.GONE);
        } else {
            holder.borderLayout.setVisibility(View.VISIBLE);
        }

        holder.settleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settleButtonListener.onClick(position, itemList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mainText;
        TextView subText;
        TextView rightText;
        MaterialButton settleButton;
        ShapeableImageView avatar;
        View borderLayout;
        ShapeableImageView deleteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mainText = itemView.findViewById(R.id.main_text);
            subText = itemView.findViewById(R.id.subtext);
            rightText = itemView.findViewById(R.id.right_text);
            settleButton = itemView.findViewById(R.id.settleButton);
            avatar = itemView.findViewById(R.id.avatar);
            borderLayout = itemView.findViewById(R.id.bottom_border_layout);
            deleteIcon = itemView.findViewById(R.id.delete);
        }
    }
}
