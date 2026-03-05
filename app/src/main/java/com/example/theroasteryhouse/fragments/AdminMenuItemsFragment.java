package com.example.theroasteryhouse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.adapters.AdminMenuAdapter;
import com.example.theroasteryhouse.R;
import com.example.theroasteryhouse.database.DatabaseHelper;
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

        loadAllSections();

        binding.adminModeMenuItemsScreenAddNewItemButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.center_panel, new AdminAddNewMenuItemFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return binding.getRoot();
    }

    private void loadAllSections() {
        setupSection(binding.recyclerCoffee, "Kawa");
        setupSection(binding.recyclerTea, "Herbata");
        setupSection(binding.recyclerDesserts, "Desery");
    }

    private void setupSection(RecyclerView recyclerView, String category) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        List<MenuItem> items = db.getMenuItemsByCategory(category);

        AdminMenuAdapter adapter = new AdminMenuAdapter(items, new AdminMenuAdapter.OnItemActionListener() {

            @Override
            public void onInfo(MenuItem item) {
                AdminEditMenuItemFragment editFragment = AdminEditMenuItemFragment.newInstance(item.getId(), item.getType());

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.center_panel, editFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
