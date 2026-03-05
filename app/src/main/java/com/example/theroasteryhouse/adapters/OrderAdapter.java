package com.example.theroasteryhouse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.ItemOrderLineBinding;
import com.example.theroasteryhouse.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<OrderItem> orderItems = new ArrayList<>();
    private OnDeleteItemListener deleteListener;
    private boolean isEditable = true;

    public interface OnDeleteItemListener {
        void onDeleteClick(int position);
    }

    public OrderAdapter(OnDeleteItemListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void addItem(OrderItem item) {
        orderItems.add(item);
        notifyItemInserted(orderItems.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < orderItems.size()) {
            orderItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderItems.size());
        }
    }

    public List<OrderItem> getItems() {
        return orderItems;
    }

    public void clear() {
        orderItems.clear();
        notifyDataSetChanged();
    }
    public void setEditable(boolean editable) {
        this.isEditable = editable;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderLineBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        holder.binding.orderItemName.setText(item.getDisplayName());

        if (item.isHeader()) {
            holder.binding.orderItemName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.binding.orderItemName.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.binding.orderItemName.setTextSize(20);
            holder.binding.orderItemPrice.setVisibility(View.GONE);
            holder.binding.btnDeleteItem.setVisibility(View.GONE);

        } else {
            holder.binding.orderItemName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.binding.orderItemName.setTextSize(18);
            holder.binding.orderItemPrice.setVisibility(View.VISIBLE);
            holder.binding.orderItemPrice.setText(String.format("%.2f zł", item.getPrice()));
            holder.binding.getRoot().setBackground(null);

            if (isEditable) {
                holder.binding.btnDeleteItem.setVisibility(View.VISIBLE);
            } else {
                holder.binding.btnDeleteItem.setVisibility(View.GONE);
            }

        }
        holder.binding.btnDeleteItem.setOnClickListener(v -> {
            if (!item.isHeader()) {
                deleteListener.onDeleteClick(holder.getAdapterPosition());
            }
        });

        if (!isEditable){
            holder.binding.btnDeleteItem.setVisibility(View.GONE);
            holder.itemView.setBackgroundResource(0);
            holder.binding.orderItemName.setBackgroundResource(0);
            holder.binding.orderItemPrice.setBackgroundResource(0);
        }
    }

    @Override
    public int getItemCount() { return orderItems.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderLineBinding binding;
        public ViewHolder(ItemOrderLineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}