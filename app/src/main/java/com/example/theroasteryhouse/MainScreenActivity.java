package com.example.theroasteryhouse;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.theroasteryhouse.databinding.ActivityMainScreenBinding;
import com.example.theroasteryhouse.databinding.FragmentMenuBinding;
import com.example.theroasteryhouse.databinding.FragmentSettingsBinding;
import com.example.theroasteryhouse.models.MenuItem;
import com.example.theroasteryhouse.models.OrderItem;

import java.util.List;

public class MainScreenActivity extends AppCompatActivity {
    private ActivityMainScreenBinding binding;
    private DatabaseHelper db;
    private OrderAdapter orderAdapter;
    private double totalPrice = 0.0;

    private String currentName;
    private String currentSurname;
    private String currentEmail;
    private String currentPassword;
    private int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);

        db = new DatabaseHelper(this);
        if (savedInstanceState != null) {
            userId = savedInstanceState.getInt("saved_userId", -1);
            totalPrice = savedInstanceState.getDouble("saved_totalPrice", 0.0);

            if (savedInstanceState.containsKey("saved_password")) {
                currentPassword = savedInstanceState.getString("saved_password");
            }
        } else {
            userId = getIntent().getIntExtra("userId", -1);
        }
        if (userId == -1) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        loadUserData();

        setupOrderPanel();
        showMenuScreen("Kawa");

        binding.leftPanelCoffeeBtn.setOnClickListener(v -> showMenuScreen("Kawa"));
        binding.leftPanelTeaBtn.setOnClickListener(v -> showMenuScreen("Herbata"));
        binding.leftPanelDessertsBtn.setOnClickListener(v -> showMenuScreen("Desery"));
        binding.leftPanelSettingsBtn.setOnClickListener(v -> {
            if ("settings".equals(binding.centerPanel.getTag())) {
                return;
            }
            showSettingsScreen();
        });
        binding.rightPanelSummaryBtn.setOnClickListener(v -> showSummaryDialog());
    }

    private void showMenuScreen(String category) {
        binding.centerPanel.setTag("menu");
        FragmentMenuBinding menuBinding =
                FragmentMenuBinding.inflate(getLayoutInflater());
        binding.centerPanel.removeAllViews();
        binding.centerPanel.addView(menuBinding.getRoot());
        menuBinding.categoryTitle.setText(category);

        menuBinding.menuRecycler.setLayoutManager(new GridLayoutManager(this, 3));

        List<MenuItem> items = db.getMenuItemsByCategory(category);

        StandardMenuAdapter adapter = new StandardMenuAdapter(items, item -> {
            onMenuItemClicked(item, category);
        });

        menuBinding.menuRecycler.setAdapter(adapter);

        LinearLayout.LayoutParams centerPanelParams =
                (LinearLayout.LayoutParams) binding.centerPanel.getLayoutParams();
        centerPanelParams.weight = 4.5f;
        binding.centerPanel.setLayoutParams(centerPanelParams);

        binding.rightPanel.setVisibility(View.VISIBLE);
    }

    private void showSettingsScreen() {
        FragmentSettingsBinding settingsBinding =
                FragmentSettingsBinding.inflate(getLayoutInflater());

        settingsBinding.settingsPagePasswordInput.setText(currentPassword);

        settingsBinding.settingsScreenCancelChangesButton.setOnClickListener(v -> {
            settingsBinding.settingsPagePasswordInput.setText(currentPassword);
                });
        settingsBinding.settingsScreenSaveChangesButton.setOnClickListener(v -> {
            String newPassword = settingsBinding.settingsPagePasswordInput.getText().toString().trim();

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Hasło nie może być puste", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = db.updateUserPassword(userId, newPassword);
            if (success) {
                currentPassword = newPassword;
                Toast.makeText(this, "Hasło zostało zmienione", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Błąd zapisu", Toast.LENGTH_SHORT).show();
            }
        });

        settingsBinding.settingsScreenLogOutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.centerPanel.removeAllViews();
        binding.centerPanel.addView(settingsBinding.getRoot());

        LinearLayout.LayoutParams centerPanelParams =
                (LinearLayout.LayoutParams) binding.centerPanel.getLayoutParams();
        LinearLayout.LayoutParams rightPanelParams =
                (LinearLayout.LayoutParams) binding.rightPanel.getLayoutParams();

        centerPanelParams.weight = 7.5f;

        binding.centerPanel.setLayoutParams(centerPanelParams);

        binding.rightPanel.setVisibility(View.GONE);
        binding.centerPanel.setTag("settings");
        binding.centerPanel.setLayoutParams(centerPanelParams);

        binding.rightPanel.setVisibility(View.GONE);

    }

    private void loadUserData() {
        if (userId != -1) {
            Cursor cursor = db.getUserById(userId);
            if (cursor != null && cursor.moveToFirst()) {

                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_FIRST_NAME);
                int surnameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_NAME);
                int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
                int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);


                if (nameIndex != -1) currentName = cursor.getString(nameIndex);
                if (surnameIndex != -1) currentSurname = cursor.getString(surnameIndex);
                if (emailIndex != -1) currentEmail = cursor.getString(emailIndex);
                if (passwordIndex != -1) currentPassword = cursor.getString(passwordIndex);

                cursor.close();
            }
        }


    }

    private void setupOrderPanel() {

        orderAdapter = new OrderAdapter(position -> {
            OrderItem item = orderAdapter.getItems().get(position);
            totalPrice -= item.getPrice();
            orderAdapter.removeItem(position);
            removeEmptyHeaders();
            updateTotalText();
        });
        binding.rightPanelOrderRecycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        binding.rightPanelOrderRecycler.setAdapter(orderAdapter);
    }
        private void onMenuItemClicked(MenuItem item, String category) {
            if ("dessert".equals(item.getType())) {
                addCategoryHeaderIfNeeded("Desery");
                OrderItem orderItem =
                        new OrderItem(item.getName(), "", item.getPriceSingle());
                addToOrderPanel(orderItem);
            } else {
                showSizeDialog(item, category);
            }
        }

    private void showSizeDialog(MenuItem item, String categoryName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_size, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView title = view.findViewById(R.id.dialog_title);
        android.widget.Button btnS = view.findViewById(R.id.btn_size_s);
        android.widget.Button btnM = view.findViewById(R.id.btn_size_m);
        android.widget.Button btnL = view.findViewById(R.id.btn_size_l);
        android.widget.Button btnCancel = view.findViewById(R.id.btn_cancel);

        title.setText(item.getName());

        if (item.getPriceS() > 0) {
            btnS.setVisibility(View.VISIBLE);
            btnS.setText(String.format("Mały - %.2f zł", item.getPriceS()));
            btnS.setOnClickListener(v -> {
                addCategoryHeaderIfNeeded(categoryName);
                addToOrderPanel(new OrderItem(item.getName(), "S", item.getPriceS()));
                dialog.dismiss();
            });
        } else {
            btnS.setVisibility(View.GONE);
        }

        if (item.getPriceM() > 0) {
            btnM.setVisibility(View.VISIBLE);
            btnM.setText(String.format("Średni - %.2f zł", item.getPriceM()));
            btnM.setOnClickListener(v -> {
                addCategoryHeaderIfNeeded(categoryName);
                addToOrderPanel(new OrderItem(item.getName(), "M", item.getPriceM()));
                dialog.dismiss();
            });
        } else {
            btnM.setVisibility(View.GONE);
        }

        if (item.getPriceL() > 0) {
            btnL.setVisibility(View.VISIBLE);
            btnL.setText(String.format("Duży - %.2f zł", item.getPriceL()));
            btnL.setOnClickListener(v -> {
                addCategoryHeaderIfNeeded(categoryName);
                addToOrderPanel(new OrderItem(item.getName(), "L", item.getPriceL()));
                dialog.dismiss();
            });
        } else {
            btnL.setVisibility(View.GONE);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

        private void addToOrderPanel(com.example.theroasteryhouse.models.OrderItem item) {
            orderAdapter.addItem(item);
            totalPrice += item.getPrice();
            updateTotalText();
        }

        private void updateTotalText() {
            if (totalPrice < 0) totalPrice = 0;
            binding.rightPanelAmountNumber.setText(String.format("%.2f zł", totalPrice));
        }

    private void showSummaryDialog() {
        if (orderAdapter.getItems().isEmpty()) {
            Toast.makeText(this, "Koszyk jest pusty!", Toast.LENGTH_SHORT).show();
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
        TextView totalValue = view.findViewById(R.id.summary_total_value);
        Button confirmBtn = view.findViewById(R.id.btn_confirm_order);
        OrderAdapter summaryAdapter = new OrderAdapter(null);

        for (com.example.theroasteryhouse.models.OrderItem item : orderAdapter.getItems()) {
            summaryAdapter.addItem(item);
        }
        summaryAdapter.setEditable(false);
        summaryRecycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        summaryRecycler.setAdapter(summaryAdapter);
        String currentTotalText = binding.rightPanelAmountNumber.getText().toString();
        double total = 0;
        for(com.example.theroasteryhouse.models.OrderItem item : orderAdapter.getItems()) {
            total += item.getPrice();
        }
        totalValue.setText(String.format("%.2f zł", total));

        confirmBtn.setOnClickListener(v -> {

            boolean success = db.insertOrder(userId, orderAdapter.getItems(), totalPrice);

            if (success) {
                Toast.makeText(this, "Zamówienie zapisane w bazie!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Błąd zapisu zamówienia", Toast.LENGTH_SHORT).show();
            }

            orderAdapter.clear();
            binding.rightPanelAmountNumber.setText("0.00 zł");
            totalPrice = 0.0;

            dialog.dismiss();
        });

        dialog.show();

        android.view.Window window = dialog.getWindow();
        if (window != null) {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().density * 800),
                    (int) (screenHeight * 0.85)
            );
        }
    }

    private void addCategoryHeaderIfNeeded(String categoryName) {
        String headerText = "--- " + categoryName + " ---";
        java.util.List<OrderItem> items = orderAdapter.getItems();

        if (items.isEmpty()) {
            orderAdapter.addItem(new OrderItem(headerText));
            return;
        }

        String lastHeaderText = "";
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).isHeader()) {
                lastHeaderText = items.get(i).getDisplayName();
                break;
            }
        }

        if (!lastHeaderText.equals(headerText)) {
            orderAdapter.addItem(new OrderItem(headerText));
        }
    }

    private void removeEmptyHeaders() {
        java.util.List<OrderItem> items = orderAdapter.getItems();

        for (int i = items.size() - 1; i >= 0; i--) {
            OrderItem item = items.get(i);

            if (item.getDisplayName().startsWith("---")) {

                if (i == items.size() - 1) {
                    orderAdapter.removeItem(i);
                }
                else if (items.get(i + 1).getDisplayName().startsWith("---")) {
                    orderAdapter.removeItem(i);
                }
            }
        }
    }
    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("saved_userId", userId);
        outState.putDouble("saved_totalPrice", totalPrice);

        if (currentPassword != null) {
            outState.putString("saved_password", currentPassword);
        }
    }
}
