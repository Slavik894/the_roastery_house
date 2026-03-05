package com.example.theroasteryhouse.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.theroasteryhouse.adapters.OrderAdapter;
import com.example.theroasteryhouse.R;
import com.example.theroasteryhouse.fragments.SpecialModeCoffeeFragment;
import com.example.theroasteryhouse.fragments.SpecialModeDessertsFragment;
import com.example.theroasteryhouse.fragments.SpecialModeTeaFragment;
import com.example.theroasteryhouse.database.DatabaseHelper;
import com.example.theroasteryhouse.databinding.ActivitySpecialModeMainScreenBinding;
import com.example.theroasteryhouse.models.Ingredient;
import com.example.theroasteryhouse.models.MenuItem;
import com.example.theroasteryhouse.models.OrderItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpecialModeMainScreenActivity extends AppCompatActivity {

    private ActivitySpecialModeMainScreenBinding binding;
    private OrderAdapter rightPanelAdapter;
     private Map<String, Ingredient> currentDrinkComponents = new LinkedHashMap<>();
    private java.util.List<OrderItem> confirmedItems = new java.util.ArrayList<>();
    private int drinksCounter = 1;
    private long additiveCounter = 0;

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

        binding.spModeLeftPanelExitBtn.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(SpecialModeMainScreenActivity.this, SelectOrderTypeActivity.class);

            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            finish();
        });
        binding.spModeAddDrinkBtn.setOnClickListener(v -> {
            onAddNextDrinkClicked();
        });
        binding.spModeSummaryBtn.setOnClickListener(v -> {
            showSummaryDialog();
        });
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
        binding.spModeRightPanelOrderRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.spModeRightPanelOrderRecycler.setAdapter(rightPanelAdapter);

        updateTotalStats();
    }

    public void addAdditiveToDrink(Ingredient ingredient) {
        additiveCounter++;
        String uniqueKey = "additive_" + additiveCounter;
        updateDrinkComponent(uniqueKey, ingredient);
    }

    public void updateDrinkComponent(String type, Ingredient ingredient) {
        currentDrinkComponents.put(type, ingredient);
        refreshRightPanelList();
    }

    private void refreshRightPanelList() {
        rightPanelAdapter.clear();
        for (OrderItem item : confirmedItems) {
            rightPanelAdapter.addItem(item);
        }
        if (!currentDrinkComponents.isEmpty()) {
            rightPanelAdapter.addItem(new OrderItem("--- Napój #" + drinksCounter + " (Edytowany) ---"));

            for (Ingredient ingredient : currentDrinkComponents.values()) {
                OrderItem item = new OrderItem(ingredient.getName(), "", ingredient.getPrice());
                rightPanelAdapter.addItem(item);
            }
        }

        updateTotalStats();
    }
    private void removeItemAt(int position) {
        if (position < 0 || position >= rightPanelAdapter.getItems().size()) return;
        if (position < confirmedItems.size()) {
            confirmedItems.remove(position);
            int headerIndex = position - 1;

            if (headerIndex >= 0 && headerIndex < confirmedItems.size()) {
                OrderItem possibleHeader = confirmedItems.get(headerIndex);
                if (possibleHeader.isHeader()) {
                    boolean isLastElement = (headerIndex == confirmedItems.size() - 1);
                    boolean isNextAlsoHeader = false;

                    if (!isLastElement) {
                        isNextAlsoHeader = confirmedItems.get(headerIndex + 1).isHeader();
                    }

                    if (isLastElement || isNextAlsoHeader) {
                        confirmedItems.remove(headerIndex);
                    }
                }
            }

            refreshRightPanelList();
            return;
        }
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
        for (OrderItem item : confirmedItems) {
            total += item.getPrice();
        }
        for (Ingredient ing : currentDrinkComponents.values()) {
            total += ing.getPrice();
        }

        binding.spModeRightPanelAmountNumber.setText(String.format(" %.2f zł", total));
    }

    private void onAddNextDrinkClicked() {
        if (currentDrinkComponents.isEmpty()) {
            return;
        }
        confirmedItems.add(new OrderItem("--- Napój #" + drinksCounter + " ---"));

        for (Ingredient ingredient : currentDrinkComponents.values()) {
            confirmedItems.add(new OrderItem(ingredient.getName(), "", ingredient.getPrice()));
        }
        currentDrinkComponents.clear();
        drinksCounter++;
        refreshRightPanelList();
    }
    public void addDessertToOrder(MenuItem dessert) {
        boolean needsHeader = true;

        if (!confirmedItems.isEmpty()) {
            for (int i = confirmedItems.size() - 1; i >= 0; i--) {
                OrderItem item = confirmedItems.get(i);

                if (item.getName().startsWith("--- ")) {
                    if (item.getName().equals("--- Deser ---")) {
                        needsHeader = false;
                    }
                    break;
                }
            }
        }

        if (needsHeader) {
            confirmedItems.add(new OrderItem("--- Deser ---"));
        }

        double correctPrice = dessert.getPriceSingle();
        if (correctPrice == 0.0) {
            correctPrice = dessert.getPriceS();
        }
        confirmedItems.add(new OrderItem(dessert.getName(), "", correctPrice));
        refreshRightPanelList();
    }

    private void showSummaryDialog() {
        if (confirmedItems.isEmpty() && currentDrinkComponents.isEmpty()) {
            android.widget.Toast.makeText(this, "Koszyk jest pusty!", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_order_summary, null);
        builder.setView(view);
        android.app.AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        androidx.recyclerview.widget.RecyclerView summaryRecycler = view.findViewById(R.id.summary_recycler);
        android.view.ViewGroup.LayoutParams params = summaryRecycler.getLayoutParams();
        params.height = (int) (getResources().getDisplayMetrics().density * 350);
        summaryRecycler.setLayoutParams(params);
        android.widget.TextView totalValue = view.findViewById(R.id.summary_total_value);
        android.widget.Button confirmBtn = view.findViewById(R.id.btn_confirm_order);
        OrderAdapter summaryAdapter = new OrderAdapter(position -> {});
        summaryAdapter.setEditable(false);
        double total = 0;
        for (OrderItem item : confirmedItems) {
            summaryAdapter.addItem(item);
            total += item.getPrice();
        }
        if (!currentDrinkComponents.isEmpty()) {
            summaryAdapter.addItem(new OrderItem("--- Napój #" + drinksCounter + " (w trakcie) ---"));
            for (Ingredient ing : currentDrinkComponents.values()) {
                OrderItem item = new OrderItem(ing.getName(), "", ing.getPrice());
                summaryAdapter.addItem(item);
                total += ing.getPrice();
            }
        }

        summaryRecycler.setLayoutManager(new LinearLayoutManager(this));
        summaryRecycler.setAdapter(summaryAdapter);
        totalValue.setText(String.format("%.2f zł", total));
        confirmBtn.setOnClickListener(v -> {
            List<OrderItem> finalOrderList = new ArrayList<>();

            finalOrderList.addAll(confirmedItems);

            if (!currentDrinkComponents.isEmpty()) {
                finalOrderList.add(new OrderItem("--- Napój #" + drinksCounter + " ---"));
                for (Ingredient ing : currentDrinkComponents.values()) {
                    finalOrderList.add(new OrderItem(ing.getName(), "", ing.getPrice()));
                }
            }

            double finalTotal = 0;
            for (OrderItem item : finalOrderList) {
                finalTotal += item.getPrice();
            }

            boolean success = new DatabaseHelper(this).insertOrder(-1, finalOrderList, finalTotal);

            if (success) {
                Toast.makeText(this, "Zamówienie przyjęte i zapisane!", Toast.LENGTH_LONG).show();
            }

            confirmedItems.clear();
            currentDrinkComponents.clear();
            drinksCounter = 1;
            additiveCounter = 0;
            refreshRightPanelList();

            dialog.dismiss();
        });

        dialog.show();
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().density * 800),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}