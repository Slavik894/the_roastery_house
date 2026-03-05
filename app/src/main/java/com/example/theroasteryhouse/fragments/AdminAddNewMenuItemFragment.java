package com.example.theroasteryhouse.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.theroasteryhouse.database.DatabaseHelper;
import com.example.theroasteryhouse.databinding.FragmentAdminAddNewMenuItemBinding;

public class AdminAddNewMenuItemFragment extends Fragment {

    private FragmentAdminAddNewMenuItemBinding binding;
    private DatabaseHelper db;
    private Uri selectedImageUri = null;

    private final String[] categories = {"Kawa", "Herbata", "Desery"};

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
                    selectedImageUri = newUri;
                    binding.adminAddNewMenuItemImage.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminAddNewMenuItemBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        setupSpinner();
        setupButtons();

        return binding.getRoot();
    }

    private void setupSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.adminAddNewMenuItemCategoryNameSpinner.setAdapter(adapter);

        binding.adminAddNewMenuItemCategoryNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                updateUIBasedOnCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateUIBasedOnCategory(String category) {
        if (category.equals("Desery")) {

            binding.adminAddNewMenuItemSizesContainer.setVisibility(View.GONE);
            binding.adminAddNewMenuItemSinglePriceInput.setVisibility(View.VISIBLE);
        } else {

            binding.adminAddNewMenuItemSizesContainer.setVisibility(View.VISIBLE);
            binding.adminAddNewMenuItemSinglePriceInput.setVisibility(View.GONE);
        }
    }

    private void setupButtons() {
        binding.adminAddNewMenuItemImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });
        binding.adminAddNewMenuItemBtnCancel.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        binding.adminAddNewMenuItemBtnSave.setOnClickListener(v -> saveMenuItem());
    }

    private void saveMenuItem() {
        String name = binding.adminAddNewMenuItemEtItemName.getText().toString().trim();
        String category = binding.adminAddNewMenuItemCategoryNameSpinner.getSelectedItem().toString();

        String imageUriString = (selectedImageUri != null) ? selectedImageUri.toString() : null;

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Podaj nazwę pozycji", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = -1;

        if (category.equals("Desery")) {
            String priceStr = binding.adminAddNewMenuItemSinglePriceInput.getText().toString();
            if (priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Podaj cenę", Toast.LENGTH_SHORT).show();
                return;
            }
            double price = Double.parseDouble(priceStr);

            result = db.addDessert(name, category, price, imageUriString);

        }
        else {

            double priceS = getPriceFromInput(binding.inputPriceS);
            double priceM = getPriceFromInput(binding.inputPriceM);
            double priceL = getPriceFromInput(binding.inputPriceL);

            if (priceS == 0 && priceM == 0 && priceL == 0) {
                Toast.makeText(getContext(), "Wprowadź przynajmniej jedną cenę", Toast.LENGTH_SHORT).show();
                return;
            }

            result = db.addDrink(name, category, priceS, priceM, priceL, imageUriString);
        }

        if (result != -1) {
            Toast.makeText(getContext(), "Dodano pomyślnie!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    private double getPriceFromInput(android.widget.EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}