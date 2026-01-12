package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.theroasteryhouse.databinding.FragmentMenuBinding;
import com.example.theroasteryhouse.models.Ingredient;
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

        binding.categoryTitle.setText("Desery");

        return binding.getRoot();
    }

    private void setupDessertsList() {
        List<MenuItem> menuItems = db.getMenuItemsByCategory("Desery");

        List<Ingredient> ingredients = new ArrayList<>();
        for (MenuItem m : menuItems) {
            ingredients.add(new Ingredient(m.getId(), m.getName(), "Pyszny deser", "", "dessert", m.getPriceSingle()));
        }
        SpecialIngredientsAdapter adapter = new SpecialIngredientsAdapter(ingredients, new SpecialIngredientsAdapter.OnItemActionListener() {
            @Override
            public void onInfo(Ingredient item) {
            }

            @Override
            public void onChoose(Ingredient item) {
                if (getActivity() instanceof SpecialModeMainScreenActivity) {
                    ((SpecialModeMainScreenActivity) getActivity()).addDessertToOrder(item);
                }
            }
        });

        binding.menuRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.menuRecycler.setAdapter(adapter);
    }
}