package com.example.theroasteryhouse;

import android.app.Activity;
import android.content.Intent;
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

        binding.adminAddNewIngredientImage.setOnClickListener(v -> openGallery());
        binding.adminAddNewIngredientSaveBtn.setOnClickListener(v -> saveIngredient());
        binding.adminAddNewIngredientCancelBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return binding.getRoot();
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
            Toast.makeText(getContext(), "Wpisz nazwę", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = db.addIngredient(name, info, uriString);
        if (id != -1) {
            Toast.makeText(getContext(), "Składnik został dodany", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }
}