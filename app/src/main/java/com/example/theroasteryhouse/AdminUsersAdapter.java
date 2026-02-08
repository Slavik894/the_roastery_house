package com.example.theroasteryhouse;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.ItemAdminUserBinding;
import com.example.theroasteryhouse.models.User;

import java.util.List;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onEdit(User user);
    }

    private List<User> users;
    private OnUserActionListener listener;

    public AdminUsersAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAdminUserBinding itemBinding = ItemAdminUserBinding.inflate(inflater, parent, false);
        return new UserViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.userFirstName.setText(user.getFirstName());
        holder.binding.userLastName.setText(user.getLastName());
        holder.binding.userEmail.setText(user.getEmail());


        holder.binding.userEditButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }


    static class UserViewHolder extends RecyclerView.ViewHolder {
        ItemAdminUserBinding binding;

        public UserViewHolder(ItemAdminUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public void setUsers(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }
}
