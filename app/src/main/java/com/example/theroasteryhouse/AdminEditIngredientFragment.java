package com.example.theroasteryhouse;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminEditIngredientInfoBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        if (getArguments() != null) {
            ingredientId = getArguments().getInt("ingredientId");
        }

        loadIngredientData();

        binding.adminEditIngredientImage.setOnClickListener(v -> openGallery());
        binding.adminEditIngredientSaveBtn.setOnClickListener(v -> saveChanges());
        binding.adminEditIngredientCancelBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return binding.getRoot();
    }

    private void loadIngredientData() {
        Cursor cursor = db.getIngredientById(ingredientId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_NAME));
            String info = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_INFO));
            String uriString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INGREDIENT_IMAGE_URI));

            binding.adminEditIngredientNameInput.setText(name);
            binding.adminEditIngredientInfoInput.setText(info);

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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void saveChanges() {
        String name = binding.adminEditIngredientNameInput.getText().toString().trim();
        String info = binding.adminEditIngredientInfoInput.getText().toString().trim();

        String uriString = (currentImageUri != null) ? currentImageUri.toString() : null;

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Nazwa nie może być pusta", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = db.updateIngredient(ingredientId, name, info, uriString);

        if (success) {
            Toast.makeText(getContext(), "Zmiany zostały zapisane", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }
}