package com.example.theroasteryhouse;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.ItemStandardMenuBinding;
import com.example.theroasteryhouse.models.Ingredient;
import com.example.theroasteryhouse.models.MenuItem;

import java.util.List;

public class SpecialDessertsAdapter extends RecyclerView.Adapter<SpecialDessertsAdapter.ViewHolder> {

    public interface OnDessertActionListener {
        void onChoose(MenuItem dessert);
    }

    private final List<MenuItem> desserts;
    private final OnDessertActionListener listener;

    public SpecialDessertsAdapter(List<MenuItem> desserts, OnDessertActionListener listener) {
        this.desserts = desserts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemStandardMenuBinding binding =
                ItemStandardMenuBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem dessert = desserts.get(position);
        holder.binding.standardMenuItemName.setText(dessert.getName());

        String uriString = dessert.getImageUri();

        if (uriString != null && !uriString.trim().isEmpty() && !uriString.equals("null")) {
            try {
                holder.binding.menuItemImage.setImageURI(android.net.Uri.parse(uriString));

                if (holder.binding.menuItemImage.getDrawable() == null) {
                    holder.binding.menuItemImage.setImageResource(R.drawable.logo);
                }
            } catch (Exception e) {
                holder.binding.menuItemImage.setImageResource(R.drawable.logo);
            }
        } else {
            holder.binding.menuItemImage.setImageResource(R.drawable.logo);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChoose(dessert);
            }
        });
    }

    @Override
    public int getItemCount() {
        return desserts != null ? desserts.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemStandardMenuBinding binding;

        public ViewHolder(@NonNull ItemStandardMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
