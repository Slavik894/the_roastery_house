package com.example.theroasteryhouse;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theroasteryhouse.databinding.AdminMenuItemBinding;
import com.example.theroasteryhouse.models.Ingredient;
import java.util.List;

public class AdminIngredientsAdapter extends RecyclerView.Adapter<AdminIngredientsAdapter.ViewHolder> {

    private List<Ingredient> list;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onDelete(Ingredient item);
        void onInfo(Ingredient item);
    }

    public AdminIngredientsAdapter(List<Ingredient> list, OnItemActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AdminMenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient item = list.get(position);
        holder.binding.menuItemName.setText(item.getName());
        holder.binding.menuItemDeleteBtn.setOnClickListener(v -> listener.onDelete(item));
        holder.binding.menuItemInfoBtn.setOnClickListener(v -> listener.onInfo(item));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AdminMenuItemBinding binding;
        public ViewHolder(AdminMenuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}