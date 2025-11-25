package com.example.theroasteryhouse;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.theroasteryhouse.databinding.FragmentAdminMenuItemsBinding;
import com.example.theroasteryhouse.models.MenuItem;

import java.util.List;

public class AdminMenuItemsFragment extends Fragment {

    private FragmentAdminMenuItemsBinding binding;
    private DatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminMenuItemsBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        loadMenuItems();

        binding.adminModeMenuItemsScreenAddNewItemButton.setOnClickListener(v -> {

        });

        return binding.getRoot();
    }

    private void loadMenuItems() {

        binding.adminModeMenuItemsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));

        List<MenuItem> items = db.getAllMenuItems();

        AdminMenuAdapter adapter = new AdminMenuAdapter(items, new AdminMenuAdapter.OnItemActionListener() {
            @Override
            public void onDelete(MenuItem item) {
                if (item.getType().equals("drink")) {
                    db.deleteDrink(item.getId());
                } else if (item.getType().equals("dessert")) {
                    db.deleteDessert(item.getId());
                }
                loadMenuItems();
            }

            @Override
            public void onInfo(MenuItem item) {
                Toast.makeText(getContext(), "Інфо про: " + item.getName(), Toast.LENGTH_SHORT).show();
                // Тут можна відкрити діалог редагування
            }
        });

        binding.adminModeMenuItemsRecycler.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}