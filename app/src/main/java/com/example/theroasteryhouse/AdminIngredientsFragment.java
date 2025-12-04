package com.example.theroasteryhouse;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.theroasteryhouse.databinding.FragmentAdminIngredientsBinding;
import com.example.theroasteryhouse.models.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class AdminIngredientsFragment extends Fragment {

    private FragmentAdminIngredientsBinding binding;
    private DatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminIngredientsBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        loadIngredients();

        binding.adminModeIngredientsScreenAddNewItemButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.center_panel, new AdminAddNewIngredientFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return binding.getRoot();
    }

    private void loadIngredients() {
        binding.adminModeIngredientsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        List<Ingredient> list = new ArrayList<>();

        Cursor cursor = db.getAllIngredients();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_NAME));
                String info = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_INFO));
                String uri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_IMAGE_URI));
                list.add(new Ingredient(id, name, info, uri));
            } while (cursor.moveToNext());
        }
        cursor.close();

        AdminIngredientsAdapter adapter = new AdminIngredientsAdapter(list, new AdminIngredientsAdapter.OnItemActionListener() {
            @Override
            public void onDelete(Ingredient item) {
                db.deleteIngredient(item.getId());
                loadIngredients();
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
        });
        binding.adminModeIngredientsRecycler.setAdapter(adapter);
    }
}