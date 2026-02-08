package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.theroasteryhouse.databinding.FragmentAdminMenuItemsBinding;
import com.example.theroasteryhouse.models.MenuItem;
import java.util.List;

public class AdminMenuItemsFragment extends Fragment {

    private FragmentAdminMenuItemsBinding binding;
    private DatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminMenuItemsBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        loadAllSections();

        binding.adminModeMenuItemsScreenAddNewItemButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.center_panel, new AdminAddNewMenuItemFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return binding.getRoot();
    }

    private void loadAllSections() {
        setupSection(binding.recyclerCoffee, "Kawa");
        setupSection(binding.recyclerTea, "Herbata");
        setupSection(binding.recyclerDesserts, "Desery");
    }

    private void setupSection(RecyclerView recyclerView, String category) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        List<MenuItem> items = db.getMenuItemsByCategory(category);

        AdminMenuAdapter adapter = new AdminMenuAdapter(items, new AdminMenuAdapter.OnItemActionListener() {
            @Override
            public void onDelete(MenuItem item) {
                showDeleteMenuItemDialog(item);
            }

            @Override
            public void onInfo(MenuItem item) {
                AdminEditMenuItemFragment editFragment = AdminEditMenuItemFragment.newInstance(item.getId(), item.getType());

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.center_panel, editFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showDeleteMenuItemDialog(MenuItem item) {
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

        message.setText("Czy na pewno chcesz usunąć\n tę pozycję z menu?");

        btnYes.setOnClickListener(v -> {
            if (item.getType().equals("drink")) {
                db.deleteDrink(item.getId());
            } else if (item.getType().equals("dessert")) {
                db.deleteDessert(item.getId());
            }
            loadAllSections();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}