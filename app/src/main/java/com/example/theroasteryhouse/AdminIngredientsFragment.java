package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theroasteryhouse.databinding.FragmentAdminIngredientsBinding;
import com.example.theroasteryhouse.models.Ingredient;
import java.util.List;

public class AdminIngredientsFragment extends Fragment {

    private FragmentAdminIngredientsBinding binding;
    private DatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminIngredientsBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        loadAllIngredients();

        binding.adminModeIngredientsScreenAddNewItemButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.center_panel, new AdminAddNewIngredientFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return binding.getRoot();
    }

    private void loadAllIngredients() {
        setupSection(binding.recyclerBeans, "beans");
        setupSection(binding.recyclerBase, "base");
        setupSection(binding.recyclerMilk, "milk");
        setupSection(binding.recyclerCoffeeAddons, "additive");
        setupSection(binding.recyclerTeaLeaves, "tea_leaves");
        setupSection(binding.recyclerTeaAddons, "tea_additive");
    }

    private void setupSection(RecyclerView recyclerView, String type) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        List<Ingredient> list = db.getIngredientsByType(type);

        AdminIngredientsAdapter adapter = new AdminIngredientsAdapter(list, new AdminIngredientsAdapter.OnItemActionListener() {
            @Override
            public void onDelete(Ingredient item) {
                showDeleteIngredientDialog(item);
            }

            @Override
            public void onInfo(Ingredient item) {
                AdminEditIngredientFragment editFragment = AdminEditIngredientFragment.newInstance(item.getId());

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.center_panel, editFragment)
                        .addToBackStack(null)
                        .commit();
            }

            private void showDeleteIngredientDialog(Ingredient item) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
                View view = getLayoutInflater().inflate(R.layout.dialog_confirm_delete, null);
                builder.setView(view);
                android.app.AlertDialog dialog = builder.create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                }

                android.widget.TextView message = view.findViewById(R.id.delete_window_title);
                android.widget.Button btnYes = view.findViewById(R.id.delete_window_confirm_button);
                android.widget.Button btnNo = view.findViewById(R.id.delete_window_cancel_button);

                message.setText("Czy na pewno chcesz usunąć\n ten składnik?");

                btnYes.setOnClickListener(v -> {
                    db.deleteIngredient(item.getId());
                    loadAllIngredients();
                    dialog.dismiss();
                });

                btnNo.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}