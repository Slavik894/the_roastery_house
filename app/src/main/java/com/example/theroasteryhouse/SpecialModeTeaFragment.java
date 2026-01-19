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

import com.example.theroasteryhouse.databinding.SpModeTeaFragmentBinding;
import com.example.theroasteryhouse.models.Ingredient;

import java.util.List;

public class SpecialModeTeaFragment extends Fragment {

    private SpModeTeaFragmentBinding binding;
    private DatabaseHelper db;

    private Ingredient selectedTeaLeaf;
    private Ingredient selectedAdditive;
    private int waterVolume = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SpModeTeaFragmentBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        setupSection(binding.spModeTeaRecyclerLeaves, "tea_leaves");
        setupSection(binding.spModeTeaRecyclerAdditives, "tea_additive");

        setupSliders();
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
                if (dbType.equals("tea_leaves")) {
                    selectedTeaLeaf = item;
                } else if (dbType.equals("tea_additive")) {
                    selectedAdditive = item;
                }

                if (getActivity() instanceof SpecialModeMainScreenActivity) {
                    SpecialModeMainScreenActivity activity = (SpecialModeMainScreenActivity) getActivity();

                    if (dbType.equals("tea_additive")) {
                        activity.addAdditiveToDrink(item);
                    } else {
                        activity.updateDrinkComponent(dbType, item);
                    }
                }
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    private void setupSliders() {
        if (binding.spModeTeaWaterSlider != null) {
            binding.spModeTeaWaterSlider.addOnChangeListener((slider, value, fromUser) -> {
                int volume = (int) value;
                if (volume > 0) {
                    Ingredient waterItem = new Ingredient(-1, "Woda: " + volume + " ml", "", "", "volume_water", 0.0);

                    if (getActivity() instanceof SpecialModeMainScreenActivity) {
                        ((SpecialModeMainScreenActivity) getActivity()).updateDrinkComponent("volume_water", waterItem);
                    }
                }
            });
        }
    }
}