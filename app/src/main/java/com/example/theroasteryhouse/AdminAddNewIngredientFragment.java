package com.example.theroasteryhouse;

import android.app.Activity;
import android.content.Intent;
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
import com.example.theroasteryhouse.databinding.FragmentAdminAddNewIngredientBinding;

public class AdminAddNewIngredientFragment extends Fragment {

    private FragmentAdminAddNewIngredientBinding binding;
    private DatabaseHelper db;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                    binding.adminAddNewIngredientImage.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminAddNewIngredientBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        setupTypeSpinner();

        binding.adminAddNewIngredientImage.setOnClickListener(v -> openGallery());
        binding.adminAddNewIngredientSaveBtn.setOnClickListener(v -> saveIngredient());
        binding.adminAddNewIngredientCancelBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return binding.getRoot();
    }

    private void setupTypeSpinner() {
        String[] ingredientTypes = {"Ziarna", "Baza kawowa", "Mleko", "Dodatek do kawy", "Liście herbaty", "Dodatek do herbaty"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ingredientTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.adminAddNewIngredientTypeSpinner.setAdapter(adapter);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void saveIngredient() {
        String name = binding.adminAddNewIngredientNameInput.getText().toString().trim();
        String info = binding.adminAddIngredientInfoInput.getText().toString().trim();
        String uriString = (selectedImageUri != null) ? selectedImageUri.toString() : "";

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Wpisz nazwę składnika", Toast.LENGTH_SHORT).show();
            return;
        }

        String priceStr = binding.adminAddNewIngredientPriceInput.getText().toString().trim();
        double price = 0.0;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr.replace(",", "."));
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Nieprawidłowa cena", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int selectedPosition = binding.adminAddNewIngredientTypeSpinner.getSelectedItemPosition();
        String typeDB;

        switch (selectedPosition) {
            case 0:
                typeDB = "beans";
                break;
            case 1:
                typeDB = "base";
                break;
            case 2:
                typeDB = "milk";
                break;
            case 3:
                typeDB = "additive";
                break;
            case 4:
                typeDB = "tea_leaves";
                break;
            case 5:
                typeDB = "tea_additive";
                break;
            default:
                typeDB = "additive";
                break;
        }


        long id = db.addIngredient(name, info, uriString, typeDB, price);

        if (id != -1) {
            Toast.makeText(getContext(), "Składnik dodany pomyślnie!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Błąd zapisu do bazy danych", Toast.LENGTH_SHORT).show();
        }
    }
}