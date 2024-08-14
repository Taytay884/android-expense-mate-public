package com.itaybs.expensemate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.itaybs.expensemate.Model.GroupListItem;
import com.itaybs.expensemate.R;

import java.util.List;

public class GroupListItemAdapter extends RecyclerView.Adapter<GroupListItemAdapter.ViewHolder> {

    private List<GroupListItem> itemList;
    private OnItemClickListener onItemClickListener;

    // Constructor to accept the listener
    public GroupListItemAdapter(List<GroupListItem> itemList, OnItemClickListener onItemClickListener) {
        this.itemList = itemList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupListItem item = itemList.get(position);
        holder.mainText.setText(item.getMainText());
        holder.subtext.setText(item.getSubtext());
        holder.rightText.setText(item.getRightText());

        // Set avatar image if necessary
        // holder.avatar.setImageResource(item.getAvatarResource());

        // Set click listener on item view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mainText;
        TextView subtext;
        TextView rightText;
        ShapeableImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            mainText = itemView.findViewById(R.id.main_text);
            subtext = itemView.findViewById(R.id.subtext);
            rightText = itemView.findViewById(R.id.right_text);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(GroupListItem item);
    }
}
