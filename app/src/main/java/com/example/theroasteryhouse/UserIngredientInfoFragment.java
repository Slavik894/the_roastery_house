package com.example.theroasteryhouse;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.theroasteryhouse.databinding.FragmentUserIngredientInfoBinding;

public class UserIngredientInfoFragment extends Fragment {

    private FragmentUserIngredientInfoBinding binding;
    private DatabaseHelper db;
    private int ingredientId;

    public static UserIngredientInfoFragment newInstance(int id) {
        UserIngredientInfoFragment fragment = new UserIngredientInfoFragment();
        Bundle args = new Bundle();
        args.putInt("ingredientId", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserIngredientInfoBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        if (getActivity() instanceof SpecialModeMainScreenActivity) {
            ((SpecialModeMainScreenActivity) getActivity()).setRightPanelVisibility(false);
        }

        if (getArguments() != null) {
            ingredientId = getArguments().getInt("ingredientId");
            loadIngredientData();
        }

        binding.spModeIngredientCancelBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return binding.getRoot();
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof SpecialModeMainScreenActivity) {
            ((SpecialModeMainScreenActivity) getActivity()).setRightPanelVisibility(true);
        }
    }

    private void loadIngredientData() {
        Cursor cursor = db.getIngredientById(ingredientId);
        if (cursor != null && cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_NAME));
            String info = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_INFO));
            String uriString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_IMAGE_URI));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_PRICE));

            String typeDB = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_TYPE));

            String displayType = "Inny";
            if (typeDB != null) {
                switch (typeDB) {
                    case "beans":
                        displayType = "Ziarna";
                        break;
                    case "base":
                        displayType = "Baza kawowa";
                        break;
                    case "milk":
                        displayType = "Mleko";
                        break;
                    case "additive":
                        displayType = "Dodatek do kawy";
                        break;
                    case "tea_leaves":
                        displayType = "Liście herbaty";
                        break;
                    case "tea_additive":
                        displayType = "Dodatek do herbaty";
                        break;
                    default:
                        displayType = typeDB;
                        break;
                }
            }
            binding.spModeIngredientNameInput.setText(name);
            binding.spModeIngredientInfoTv.setText((info == null || info.isEmpty()) ? "Brak opisu." : info);
            binding.spModeIngredientPriceInput.setText(String.format("Cena: %.2f zł", price));
            binding.spModeIngredientTypeTv.setText(displayType);

            if (uriString != null && !uriString.isEmpty()) {
                try {
                    binding.spModeIngredientInfoImage.setImageURI(Uri.parse(uriString));
                } catch (Exception e) {
                    binding.spModeIngredientInfoImage.setImageResource(R.drawable.logo);
                }
            } else {
                binding.spModeIngredientInfoImage.setImageResource(R.drawable.logo);
            }
        }
        if (cursor != null) cursor.close();
    }
}