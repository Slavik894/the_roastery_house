package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.theroasteryhouse.databinding.FragmentMenuBinding;
import com.example.theroasteryhouse.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class SpecialModeDessertsFragment extends Fragment {

    private FragmentMenuBinding binding;
    private DatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());
        setupDessertsList();

        binding.categoryTitle.setText("Tryb specjalny\nDesery");
        binding.categoryTitle.setTextSize(28);

        return binding.getRoot();
    }

    private void setupDessertsList() {
        List<MenuItem> menuItems = db.getMenuItemsByCategory("Desery");

        SpecialDessertsAdapter adapter = new SpecialDessertsAdapter(
                menuItems,
                dessert -> {
                    if (getActivity() instanceof SpecialModeMainScreenActivity) {
                        ((SpecialModeMainScreenActivity) getActivity())
                                .addDessertToOrder(dessert);
                    }
                });

        binding.menuRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.menuRecycler.setAdapter(adapter);
    }
}