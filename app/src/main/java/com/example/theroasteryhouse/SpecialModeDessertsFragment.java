package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.theroasteryhouse.databinding.FragmentMenuBinding; // Можемо перевикористати існуючий layout меню
import com.example.theroasteryhouse.models.MenuItem;
import java.util.List;

public class SpecialModeDessertsFragment extends Fragment {

    private FragmentMenuBinding binding;
    private DatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());
        setupDessertsList();

        binding.categoryTitle.setText("Desery");

        return binding.getRoot();
    }

    private void setupDessertsList() {
        List<MenuItem> desserts = db.getMenuItemsByCategory("Desery");

        StandardMenuAdapter adapter = new StandardMenuAdapter(desserts, item -> {
         // Логіка кліку: додати десерт у праве вікно замовлення
        //     ((SpecialModeMainScreenActivity) requireActivity()).addToOrder(item);
        });

        binding.menuRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.menuRecycler.setAdapter(adapter);

        // Примітка: Тобі треба розкоментувати рядки вище, коли буде готовий StandardMenuAdapter
    }
}