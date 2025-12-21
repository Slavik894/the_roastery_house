package com.example.theroasteryhouse;

import android.view.LayoutInflater;
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
        holder.binding.orderItemPrice.setText(String.format("%.2f zł", item.getPrice()));

        holder.binding.btnDeleteItem.setOnClickListener(v -> {
            deleteListener.onDeleteClick(holder.getAdapterPosition());
        });
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