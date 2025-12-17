package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.SpModeCofeeFragmentBinding;
import com.example.theroasteryhouse.models.Ingredient;

import java.util.List;

public class SpecialModeCoffeeFragment extends Fragment {

    private SpModeCofeeFragmentBinding binding;
    private DatabaseHelper db;

    private Ingredient selectedBean;
    private Ingredient selectedBase;
    private Ingredient selectedMilkType;
    private Ingredient selectedAdditive;

    private int waterVolume = 0;
    private int milkVolume = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SpModeCofeeFragmentBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());


        setupSection(binding.spModeCofeeRecyclerBeans, "beans");
        setupSection(binding.spModeCoffeeRecyclerDrinkBase, "base");
        setupSection(binding.spModeCoffeeRecyclerMilkType, "milk");
        setupSection(binding.spModeCoffeeRecyclerAdditives, "additive");

        setupSliders();

        return binding.getRoot();
    }

    private void setupSection(RecyclerView recyclerView, String dbType) {
        List<Ingredient> ingredients = db.getIngredientsByType(dbType);

        SpecialIngredientsAdapter adapter = new SpecialIngredientsAdapter(ingredients, new SpecialIngredientsAdapter.OnItemActionListener() {
            @Override
            public void onInfo(Ingredient item) {
                Toast.makeText(getContext(), item.getInfo(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChoose(Ingredient item) {
                saveSelection(dbType, item);

                // Тут можна додати логіку оновлення Правої Панелі
                // updateRightPanel();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    private void saveSelection(String type, Ingredient item) {
        switch (type) {
            case "beans":
                selectedBean = item;

                break;
            case "base":
                selectedBase = item;
                break;
            case "milk":
                selectedMilkType = item;
                break;
            case "additive":
                selectedAdditive = item;
                break;
        }
    }

    private void setupSliders() {
        // Слайдер води
        if (binding.spModeCoffeeWaterSlider != null) {
            binding.spModeCoffeeWaterSlider.addOnChangeListener((slider, value, fromUser) -> {
                waterVolume = (int) value;
            });
        }
        if (binding.spModeCoffeeMilkSlider != null) {
            binding.spModeCoffeeMilkSlider.addOnChangeListener((slider, value, fromUser) -> {
                milkVolume = (int) value;
            });
        }
    }
}