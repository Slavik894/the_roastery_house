package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.theroasteryhouse.databinding.ActivitySpecialModeMainScreenBinding;
import com.example.theroasteryhouse.models.Ingredient;
import com.example.theroasteryhouse.models.OrderItem;

import java.util.HashMap;
import java.util.Map;

public class SpecialModeMainScreenActivity extends AppCompatActivity {

    private ActivitySpecialModeMainScreenBinding binding;
    private OrderAdapter rightPanelAdapter;
     private Map<String, Ingredient> currentDrinkComponents = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpecialModeMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRightPanel();

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            loadFragment(new SpecialModeCoffeeFragment(), "coffee");
        }

        binding.spModeLeftPanelCoffeeBtn.setOnClickListener(v -> {
            loadFragment(new SpecialModeCoffeeFragment(), "coffee");
        });

        binding.spModeLeftPanelTeaBtn.setOnClickListener(v -> {
            loadFragment(new SpecialModeTeaFragment(), "tea");
        });

        binding.spModeLeftPanelDessertsBtn.setOnClickListener(v -> {
            loadFragment(new SpecialModeDessertsFragment(), "desserts");
        });

        binding.spModeLeftPanelExitBtn.setOnClickListener(v -> finish());
    }

    private void loadFragment(Fragment fragment, String type) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sp_mode_center_panel, fragment)
                .commit();

        updateUIForType(type);
    }

    private void updateUIForType(String type) {
        int darkBrown = getColor(R.color.functional_buttons_color);
        int lightBrown = getColor(R.color.form_button_pressed_color);

        if (type.equals("desserts")) {
            binding.spModeAddDrinkBtn.setVisibility(View.GONE);
        } else {
            binding.spModeAddDrinkBtn.setVisibility(View.VISIBLE);
        }
    }

    public void setRightPanelVisibility(boolean isVisible) {
        // 1. Керуємо видимістю правої панелі
        if (binding.spModeRightPanel != null) {
            binding.spModeRightPanel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
        android.widget.LinearLayout.LayoutParams params =
                (android.widget.LinearLayout.LayoutParams) binding.spModeCenterPanel.getLayoutParams();

        if (isVisible) {
            params.weight = 4.5f;
        } else {
            params.weight = 7.5f;
        }
        binding.spModeCenterPanel.setLayoutParams(params);
    }

        private void setupRightPanel() {
        rightPanelAdapter = new OrderAdapter(position -> {
            removeItemAt(position);
        });

        // Прив'язуємо адаптер до RecyclerView
        // Переконайся, що в XML ID списку саме spModeRightPanelRecycler
        binding.spModeRightPanelOrderRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.spModeRightPanelOrderRecycler.setAdapter(rightPanelAdapter);

        updateTotalStats();
    }

    public void updateDrinkComponent(String type, Ingredient ingredient) {
        currentDrinkComponents.put(type, ingredient);
        refreshRightPanelList();
    }

    private void refreshRightPanelList() {
        rightPanelAdapter.clear();
        for (Ingredient ingredient : currentDrinkComponents.values()) {
            OrderItem item = new OrderItem(ingredient.getName(), "", ingredient.getPrice());
            rightPanelAdapter.addItem(item);
        }

        updateTotalStats();
    }
    private void removeItemAt(int position) {
        if (position < 0 || position >= rightPanelAdapter.getItems().size()) return;

        OrderItem itemToRemove = rightPanelAdapter.getItems().get(position);
        String keyToRemove = null;
        for (Map.Entry<String, Ingredient> entry : currentDrinkComponents.entrySet()) {
            if (entry.getValue().getName().equals(itemToRemove.getName())) {
                keyToRemove = entry.getKey();
                break;
            }
        }

        if (keyToRemove != null) {
            currentDrinkComponents.remove(keyToRemove);
            refreshRightPanelList();
        }
    }

    private void updateTotalStats() {
        double total = 0;
        for (Ingredient ing : currentDrinkComponents.values()) {
            total += ing.getPrice();
        }
        binding.spModeRightPanelAmountNumber.setText(String.format(" %.2f zł", total));
    }
}