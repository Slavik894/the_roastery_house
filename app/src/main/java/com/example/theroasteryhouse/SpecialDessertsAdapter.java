package com.example.theroasteryhouse;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.ItemStandardMenuBinding;
import com.example.theroasteryhouse.models.Ingredient;

import java.util.List;

public class SpecialDessertsAdapter extends RecyclerView.Adapter<SpecialDessertsAdapter.ViewHolder> {

    public interface OnDessertActionListener {
        void onChoose(Ingredient dessert);
    }

    private final List<Ingredient> desserts;
    private final OnDessertActionListener listener;

    public SpecialDessertsAdapter(List<Ingredient> desserts, OnDessertActionListener listener) {
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
        Ingredient dessert = desserts.get(position);
        holder.binding.standardMenuItemName.setText(dessert.getName());

        holder.binding.standardMenuItemName.setOnClickListener(v -> {
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
