package com.itaybs.expensemate.Adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.itaybs.expensemate.Interface.OnDeleteListItemListener;
import com.itaybs.expensemate.Model.ExpenseListItem;
import com.itaybs.expensemate.R;

import java.util.List;

public class ExpenseListItemAdapter extends RecyclerView.Adapter<ExpenseListItemAdapter.ViewHolder> {

    private boolean showDeleteIcon = false;
    private OnDeleteListItemListener onDeleteExpenseListener;
    private List<ExpenseListItem> itemList;

    public ExpenseListItemAdapter(List<ExpenseListItem> itemList, OnDeleteListItemListener onDeleteExpenseListener) {
        this.itemList = itemList;
        this.onDeleteExpenseListener = onDeleteExpenseListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        ExpenseListItem item = itemList.get(position);
        holder.avatar.setImageResource(item.getAvatarImageResource());
        holder.avatar.setImageTintList(ColorStateList.valueOf(item.getAvatarColor()));
        holder.mainText.setText(item.getMainText());
        holder.subText.setText(item.getSubtext());

        if (showDeleteIcon) {
            holder.deleteIcon.setVisibility(View.VISIBLE);
        } else {
            holder.rightText.setText(item.getRightText());
        }

        // Show/hide border based on position
        if (position == itemList.size() - 1) {
            holder.borderLayout.setVisibility(View.GONE);
        } else {
            holder.borderLayout.setVisibility(View.VISIBLE);
        }

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            this.showDeleteIcon = !showDeleteIcon;
            if (showDeleteIcon) {
                holder.deleteIcon.setVisibility(View.VISIBLE);
                holder.rightText.setVisibility(View.GONE);
            } else {
                holder.deleteIcon.setVisibility(View.GONE);
                holder.rightText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void removeItem(int position) {
        onDeleteExpenseListener.onDeleteClick(position, itemList.get(position).getId());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mainText;
        TextView subText;
        TextView rightText;
        ShapeableImageView avatar;
        View borderLayout;
        ShapeableImageView deleteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mainText = itemView.findViewById(R.id.main_text);
            subText = itemView.findViewById(R.id.subtext);
            rightText = itemView.findViewById(R.id.right_text);
            avatar = itemView.findViewById(R.id.avatar);
            borderLayout = itemView.findViewById(R.id.bottom_border_layout);
            deleteIcon = itemView.findViewById(R.id.delete);
        }
    }
}
