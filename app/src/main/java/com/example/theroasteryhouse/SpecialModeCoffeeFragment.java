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
        setupRadioButtons();

        return binding.getRoot();
    }

    private void setupSection(RecyclerView recyclerView, String dbType) {
        List<Ingredient> ingredients = db.getIngredientsByType(dbType);

        SpecialIngredientsAdapter adapter = new SpecialIngredientsAdapter(ingredients, new SpecialIngredientsAdapter.OnItemActionListener() {
            @Override
            public void onInfo(Ingredient item) {
                UserIngredientInfoFragment infoFragment = UserIngredientInfoFragment.newInstance(item.getId());

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.sp_mode_center_panel, infoFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onChoose(Ingredient item) {
                saveSelection(dbType, item);
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
        if (getActivity() instanceof SpecialModeMainScreenActivity) {
            ((SpecialModeMainScreenActivity) getActivity())
                    .updateDrinkComponent(type, item);
        }
    }

    private void setupSliders() {
        // Слайдер води
        if (binding.spModeCoffeeWaterSlider != null) {
            binding.spModeCoffeeWaterSlider.addOnChangeListener((slider, value, fromUser) -> {
                int volume = (int) value;
                if (volume > 0) {
                    Ingredient waterItem = new Ingredient(-1, "Woda: " + volume + " ml", "", "", "volume_water", 0.0);

                    if (getActivity() instanceof SpecialModeMainScreenActivity) {
                        ((SpecialModeMainScreenActivity) getActivity()).updateDrinkComponent("volume_water", waterItem);
                    }
                }
            });
        }

        if (binding.spModeCoffeeMilkSlider != null) {
            binding.spModeCoffeeMilkSlider.addOnChangeListener((slider, value, fromUser) -> {
                int volume = (int) value;
                if (volume > 0) {
                    Ingredient milkItem = new Ingredient(-1, "Mleko: " + volume + " ml", "", "", "volume_milk", 0.0);

                    if (getActivity() instanceof SpecialModeMainScreenActivity) {
                        ((SpecialModeMainScreenActivity) getActivity()).updateDrinkComponent("volume_milk", milkItem);
                    }
                }
            });
        }
    }
    private void setupRadioButtons() {
        if (binding.spModeCoffeeMilkTeksture != null) {
            binding.spModeCoffeeMilkTeksture.setOnCheckedChangeListener((group, checkedId) -> {
                android.widget.RadioButton checkedBtn = group.findViewById(checkedId);
                if (checkedBtn != null) {
                    String textureName = checkedBtn.getText().toString();

                    Ingredient textureItem = new Ingredient(-1, "Tekstura: " + textureName, "", "", "milk_texture", 0.0);

                    if (getActivity() instanceof SpecialModeMainScreenActivity) {
                        ((SpecialModeMainScreenActivity) getActivity())
                                .updateDrinkComponent("milk_texture", textureItem);
                    }
                }
            });
        }
    }
}