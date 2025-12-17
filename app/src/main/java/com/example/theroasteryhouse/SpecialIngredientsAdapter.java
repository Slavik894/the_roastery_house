package com.example.theroasteryhouse;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theroasteryhouse.databinding.SpModeIngredientItemBinding;
import com.example.theroasteryhouse.models.Ingredient;
import java.util.List;

public class SpecialIngredientsAdapter extends RecyclerView.Adapter<SpecialIngredientsAdapter.ViewHolder> {

    private List<Ingredient> items;
    private OnItemActionListener listener;
    private int selectedPosition = -1;

    public interface OnItemActionListener {
        void onInfo(Ingredient item);
        void onChoose(Ingredient item);
    }

    public SpecialIngredientsAdapter(List<Ingredient> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(SpModeIngredientItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient item = items.get(position);
        holder.binding.spModeItemName.setText(item.getName());


        holder.binding.spModeItemInfoBtn.setOnClickListener(v -> listener.onInfo(item));

        holder.binding.spModeChooseBtn.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            listener.onChoose(item);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SpModeIngredientItemBinding binding;
        public ViewHolder(@NonNull SpModeIngredientItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}