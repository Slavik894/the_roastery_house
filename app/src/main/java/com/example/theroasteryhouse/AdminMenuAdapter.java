package com.example.theroasteryhouse;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.AdminMenuItemBinding;
import com.example.theroasteryhouse.models.MenuItem;

import java.util.List;

public class AdminMenuAdapter extends RecyclerView.Adapter<AdminMenuAdapter.MenuViewHolder> {

    private List<MenuItem> items;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onInfo(MenuItem item);
    }

    public AdminMenuAdapter(List<MenuItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdminMenuItemBinding binding = AdminMenuItemBinding.inflate(inflater, parent, false);

        return new MenuViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = items.get(position);

        holder.binding.menuItemInfoBtn.setOnClickListener(v -> listener.onInfo(item));
        holder.binding.menuItemName.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {

        AdminMenuItemBinding binding;

        public MenuViewHolder(AdminMenuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}