package com.example.theroasteryhouse;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.theroasteryhouse.databinding.FragmentAdminEditIngredientInfoBinding;

public class AdminEditIngredientFragment extends Fragment {

    private FragmentAdminEditIngredientInfoBinding binding;
    private DatabaseHelper db;

    private int ingredientId;
    private Uri currentImageUri = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri newUri = result.getData().getData();
                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                newUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    currentImageUri = newUri;
                    binding.adminEditIngredientImage.setImageURI(currentImageUri);
                }
            }
    );

    public static AdminEditIngredientFragment newInstance(int id) {
        AdminEditIngredientFragment fragment = new AdminEditIngredientFragment();
        Bundle args = new Bundle();
        args.putInt("ingredientId", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminEditIngredientInfoBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        if (getArguments() != null) {
            ingredientId = getArguments().getInt("ingredientId");
        }

        setupTypeSpinner();
        loadIngredientData();

        binding.adminEditIngredientImage.setOnClickListener(v -> openGallery());
        binding.adminEditIngredientSaveBtn.setOnClickListener(v -> saveChanges());
        binding.adminEditIngredientDeleteBtn.setOnClickListener(v -> showDeleteIngredientDialog());

        return binding.getRoot();
    }

    private void setupTypeSpinner() {
        String[] ingredientTypes = {"Ziarna", "Baza kawowa", "Mleko", "Dodatek do kawy", "Liście do herbaty", "Dodatek do herbaty" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ingredientTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.adminEditIngredientTypeSpinner.setAdapter(adapter);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void loadIngredientData() {
        Cursor cursor = db.getIngredientById(ingredientId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_NAME));
            String info = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_INFO));
            String uriString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_IMAGE_URI));

            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_TYPE));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_PRICE));

            binding.adminEditIngredientNameInput.setText(name);
            binding.adminEditIngredientInfoInput.setText(info);
            binding.adminEditIngredientPriceInput.setText(String.valueOf(price));

            setSpinnerSelection(type);

            if (uriString != null && !uriString.isEmpty()) {
                currentImageUri = Uri.parse(uriString);
                try {
                    binding.adminEditIngredientImage.setImageURI(currentImageUri);
                } catch (Exception e) {
                    binding.adminEditIngredientImage.setImageResource(R.drawable.logo);
                }
            } else {
                binding.adminEditIngredientImage.setImageResource(R.drawable.logo);
            }
        }
        if (cursor != null) cursor.close();
    }
    private void setSpinnerSelection(String typeDB) {
        if (typeDB == null) return;

        int position = 3;
        switch (typeDB) {
            case "beans": position = 0; break;
            case "base": position = 1; break;
            case "milk": position = 2; break;
            case "additive": position = 3; break;
            case "tea_leaves": position = 4; break;
            case "tea_additive": position = 5; break;
        }
        binding.adminEditIngredientTypeSpinner.setSelection(position);
    }

    private void saveChanges() {
        String name = binding.adminEditIngredientNameInput.getText().toString().trim();
        String info = binding.adminEditIngredientInfoInput.getText().toString().trim();
        String uriString = (currentImageUri != null) ? currentImageUri.toString() : null;

        String priceStr = binding.adminEditIngredientPriceInput.getText().toString().trim();
        double price = 0.0;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr.replace(",", "."));
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Nieprawidłowa cena", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int selectedPosition = binding.adminEditIngredientTypeSpinner.getSelectedItemPosition();
        String typeDB;
        switch (selectedPosition) {
            case 0: typeDB = "beans"; break;
            case 1: typeDB = "base"; break;
            case 2: typeDB = "milk"; break;
            case 3: typeDB = "additive"; break;
            case 4: typeDB = "tea_leaves"; break;
            case 5: typeDB = "tea_additive"; break;
            default: typeDB = "additive"; break;
        }

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Nazwa nie może być pusta", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = db.updateIngredient(ingredientId, name, info, uriString, typeDB, price);

        if (success) {
            Toast.makeText(getContext(), "Zmiany zostały zapisane", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Błąd zapisu", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteIngredientDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(view);
        android.app.AlertDialog dialog = builder.create();
        android.widget.TextView message = view.findViewById(R.id.delete_window_title);
        android.widget.Button btnYes = view.findViewById(R.id.delete_window_confirm_button);
        android.widget.Button btnNo = view.findViewById(R.id.delete_window_cancel_button);

        message.setText("Czy na pewno chcesz usunąć\n ten składnik?");

        btnYes.setOnClickListener(v -> {
            boolean success = db.deleteIngredient(ingredientId);

            if (success) {
                Toast.makeText(getContext(), "Składnik został usunięty", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Błąd podczas usuwania", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
            getParentFragmentManager().popBackStack();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}