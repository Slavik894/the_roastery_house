package com.example.theroasteryhouse.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.theroasteryhouse.R;
import com.example.theroasteryhouse.database.DatabaseHelper;
import com.example.theroasteryhouse.databinding.FragmentAdminEditMenuItemBinding;

public class AdminEditMenuItemFragment extends Fragment {

    private FragmentAdminEditMenuItemBinding binding;
    private DatabaseHelper db;
    private final String[] categories = {"Kawa", "Herbata", "Desery"};

    private int itemId;
    private String itemType;
    private String currentItemImageUri;

    // Лаунчер для вибору фото
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri newUri = result.getData().getData();
                    try {
                        // Надаємо права на постійний доступ до файлу
                        requireContext().getContentResolver().takePersistableUriPermission(
                                newUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    // Зберігаємо URI у змінну та оновлюємо картинку на екрані
                    currentItemImageUri = newUri.toString();
                    binding.adminEditMenuItemImage.setImageURI(newUri);
                }
            }
    );

    public static AdminEditMenuItemFragment newInstance(int id, String type) {
        AdminEditMenuItemFragment fragment = new AdminEditMenuItemFragment();
        Bundle args = new Bundle();
        args.putInt("itemId", id);
        args.putString("itemType", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminEditMenuItemBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        if (getArguments() != null) {
            itemId = getArguments().getInt("itemId");
            itemType = getArguments().getString("itemType");
        }

        setupSpinner();
        loadItemData();
        setupButtons();

        binding.adminEditMenuItemImage.setOnClickListener(v -> openGallery());

        return binding.getRoot();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.adminEditMenuItemCategorySpinner.setAdapter(adapter);

        binding.adminEditMenuItemCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        if (category.equals("Desery") || category.equals("Dodatki")) {
            binding.adminEditMenuItemSizesContainer.setVisibility(View.GONE);
            binding.adminEditMenuItemSinglePriceInput.setVisibility(View.VISIBLE);
        } else {
            binding.adminEditMenuItemSizesContainer.setVisibility(View.VISIBLE);
            binding.adminEditMenuItemSinglePriceInput.setVisibility(View.GONE);
        }
    }

    private void loadItemData() {
        Cursor cursor = null;
        try {
            if ("drink".equals(itemType)) {
                cursor = db.getDrinkById(itemId);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DRINK_NAME));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DRINK_CATEGORY));
                    double priceS = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DRINK_PRICE_S));
                    double priceM = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DRINK_PRICE_M));
                    double priceL = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DRINK_PRICE_L));
                    currentItemImageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MENU_IMAGE_URI));


                    binding.adminEditMenuItemNameInput.setText(name);
                    setSpinnerToValue(category);

                    if (priceS > 0) {
                        binding.cbEditSizeS.setChecked(true);
                        binding.inputEditPriceS.setText(String.valueOf(priceS));
                    }
                    if (priceM > 0) {
                        binding.cbEditSizeM.setChecked(true);
                        binding.inputEditPriceM.setText(String.valueOf(priceM));
                    }
                    if (priceL > 0) {
                        binding.cbEditSizeL.setChecked(true);
                        binding.inputEditPriceL.setText(String.valueOf(priceL));
                    }
                    if (currentItemImageUri != null && !currentItemImageUri.isEmpty()) {
                        try {
                            binding.adminEditMenuItemImage.setImageURI(Uri.parse(currentItemImageUri));
                        } catch (Exception e) {
                            binding.adminEditMenuItemImage.setImageResource(R.drawable.logo);
                        }
                    } else {
                        binding.adminEditMenuItemImage.setImageResource(R.drawable.logo);
                    }
                }
            } else if ("dessert".equals(itemType)) {
                cursor = db.getDessertById(itemId);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESSERT_NAME));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESSERT_CATEGORY));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESSERT_PRICE));
                    currentItemImageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MENU_IMAGE_URI));

                    binding.adminEditMenuItemNameInput.setText(name);
                    setSpinnerToValue(category);
                    binding.adminEditMenuItemSinglePriceInput.setText(String.valueOf(price));

                    if (currentItemImageUri != null && !currentItemImageUri.isEmpty()) {
                        try {
                            binding.adminEditMenuItemImage.setImageURI(Uri.parse(currentItemImageUri));
                        } catch (Exception e) {
                            binding.adminEditMenuItemImage.setImageResource(R.drawable.logo);
                        }
                    } else {
                        binding.adminEditMenuItemImage.setImageResource(R.drawable.logo);
                    }
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void setSpinnerToValue(String value) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(value)) {
                binding.adminEditMenuItemCategorySpinner.setSelection(i);
                break;
            }
        }
    }

    private void setupButtons() {
        binding.adminEditMenuItemBtnDelete.setOnClickListener(v -> {
            showDeleteMenuItemDialog();
        });

        binding.adminEditMenuItemBtnSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String name = binding.adminEditMenuItemNameInput.getText().toString().trim();
        String category = binding.adminEditMenuItemCategorySpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Nazwa nie może być pusta", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = false;

        if (category.equals("Desery") || category.equals("Dodatki")) {
            double price = getPriceFromInput(binding.adminEditMenuItemSinglePriceInput);
            success = db.updateDessert(itemId, name, category, price, currentItemImageUri);
        } else {
            double priceS = getPriceFromInput(binding.inputEditPriceS);
            double priceM = getPriceFromInput(binding.inputEditPriceM);
            double priceL = getPriceFromInput(binding.inputEditPriceL);

            success = db.updateDrink(itemId, name, category, priceS, priceM, priceL, currentItemImageUri);
        }

        if (success) {
            Toast.makeText(getContext(), "Zmiany zostały zapisane", Toast.LENGTH_SHORT).show();
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

    private void showDeleteMenuItemDialog() {
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

        message.setText("Czy na pewno chcesz usunąć\n tę pozycję menu?");

        btnYes.setOnClickListener(v -> {
            if ("drink".equals(itemType)) {
                db.deleteDrink(itemId);
            } else if ("dessert".equals(itemType)) {
                db.deleteDessert(itemId);
            }

            Toast.makeText(getContext(), "Pozycja usunięta", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            getParentFragmentManager().popBackStack();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }


}
