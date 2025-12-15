package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.SpModeCofeeFragmentBinding;
import com.example.theroasteryhouse.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class SpecialModeCoffeeFragment extends Fragment {

    private SpModeCofeeFragmentBinding binding;
    private DatabaseHelper db;

    // Змінні для зберігання вибору користувача
    private Ingredient selectedBean;
    private Ingredient selectedBase;
    private Ingredient selectedMilkType;
    private Ingredient selectedAdditive;
    private int waterVolume = 0;
    private int milkVolume = 0;
    private String milkTexture = "Still milk";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SpModeCofeeFragmentBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        // 1. Налаштовуємо списки
        setupRecyclerView(binding.spModeCofeeRecyclerBeans, "beans");
        setupRecyclerView(binding.spModeCoffeeRecyclerDrinkBase, "base");
        setupRecyclerView(binding.spModeCoffeeRecyclerMilkType, "milk");
        setupRecyclerView(binding.spModeCoffeeRecyclerAdditives, "additive");

        // 2. Налаштовуємо слайдери
        binding.spModeCoffeeWaterSlider.addOnChangeListener((slider, value, fromUser) -> {
            waterVolume = (int) value;
            // Тут можна оновити якийсь TextView, якщо хочеш показати число
        });

        binding.spModeCoffeeMilkSlider.addOnChangeListener((slider, value, fromUser) -> {
            milkVolume = (int) value;
        });

        // 3. Radio Group (Пінка чи ні)
        binding.spModeCoffeeMilkTeksture.setOnCheckedChangeListener((group, checkedId) -> {
            // Тут перевіряємо, який ID вибрано.
            // Потрібно дати ID радіокнопкам в XML, наприклад @+id/rb_foam і @+id/rb_still
            /*
            if (checkedId == R.id.rb_foam) milkTexture = "Foam";
            else milkTexture = "Still";
            */
        });

        return binding.getRoot();
    }

    private void setupRecyclerView(RecyclerView recyclerView, String category) {
        // Встановлюємо сітку по 3 елементи
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Отримуємо дані з БД (Тут треба буде допрацювати логіку фільтрації)
        // Наприклад, створити в DatabaseHelper метод getIngredientsByType(String type)
        // Поки що завантажимо всі для прикладу:
        List<Ingredient> list = new ArrayList<>();
        // list = db.getIngredientsByType(category); <--- Це треба реалізувати в БД

        // ТИМЧАСОВИЙ КОД ДЛЯ ТЕСТУ (ВИДАЛИТИ ПОТІМ):
        list.add(new Ingredient(1, "Test " + category, "Info", ""));
        list.add(new Ingredient(2, "Test 2", "Info", ""));
        list.add(new Ingredient(3, "Test 3", "Info", ""));
        // ------------------------------------------

        SpecialIngredientsAdapter adapter = new SpecialIngredientsAdapter(list, new SpecialIngredientsAdapter.OnItemActionListener() {
            @Override
            public void onInfo(Ingredient item) {
                // Відкрити діалог з інформацією
                Toast.makeText(getContext(), "Info: " + item.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChoose(Ingredient item) {
                // Зберігаємо вибір залежно від категорії
                switch (category) {
                    case "beans": selectedBean = item; break;
                    case "base": selectedBase = item; break;
                    case "milk": selectedMilkType = item; break;
                    case "additive": selectedAdditive = item; break;
                }

                // Тут можна викликати метод Activity, щоб оновити праву панель!
                // ((SpecialModeActivity) requireActivity()).updateRightPanel(...);
            }
        });

        recyclerView.setAdapter(adapter);
    }
}