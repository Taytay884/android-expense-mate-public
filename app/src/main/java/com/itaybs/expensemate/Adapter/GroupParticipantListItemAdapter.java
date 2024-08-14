package com.itaybs.expensemate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.itaybs.expensemate.Model.GroupParticipantListItem;
import com.itaybs.expensemate.R;

import java.util.List;

public class GroupParticipantListItemAdapter extends RecyclerView.Adapter<GroupParticipantListItemAdapter.ViewHolder> {

    private List<GroupParticipantListItem> itemList;

    public GroupParticipantListItemAdapter(List<GroupParticipantListItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        GroupParticipantListItem item = itemList.get(position);
        holder.mainText.setText(item.getMainText());

        if (item.getShowDeleteIcon()) {
            holder.deleteIcon.setVisibility(View.VISIBLE);
        } else {
            holder.rightText.setText(item.getRightText());
        }

        // Set avatar image if necessary
        // holder.avatar.setImageResource(item.getAvatarResource());

        // Show/hide border based on position
        if (position == itemList.size() - 1) {
            holder.borderLayout.setVisibility(View.GONE);
        } else {
            holder.borderLayout.setVisibility(View.VISIBLE);
        }

        // Set delete icon click listener
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Method to remove item from the list
    private void removeItem(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());

        // In order to remove the border of the last item
        notifyItemChanged(itemList.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mainText;
        TextView rightText;
        ShapeableImageView avatar;
        View borderLayout;
        ShapeableImageView deleteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mainText = itemView.findViewById(R.id.main_text);
            rightText = itemView.findViewById(R.id.right_text);
            avatar = itemView.findViewById(R.id.avatar);
            borderLayout = itemView.findViewById(R.id.bottom_border_layout);
            deleteIcon = itemView.findViewById(R.id.delete);
        }
    }
}
