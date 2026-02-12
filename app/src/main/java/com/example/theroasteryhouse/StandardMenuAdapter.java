package com.example.theroasteryhouse;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theroasteryhouse.databinding.ItemStandardMenuBinding;
import com.example.theroasteryhouse.models.MenuItem;
import java.util.List;

public class StandardMenuAdapter extends RecyclerView.Adapter<StandardMenuAdapter.ViewHolder> {

    private List<MenuItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    public StandardMenuAdapter(List<MenuItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateList(List<MenuItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemStandardMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = items.get(position);
        holder.binding.standardMenuItemName.setText(item.getName());


        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            try {
                holder.binding.menuItemImage.setImageURI(android.net.Uri.parse(item.getImageUri()));
            } catch (Exception e) {
                holder.binding.menuItemImage.setImageResource(R.drawable.logo);
            }
        } else {
            holder.binding.menuItemImage.setImageResource(R.drawable.logo);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemStandardMenuBinding binding;
        public ViewHolder(ItemStandardMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}