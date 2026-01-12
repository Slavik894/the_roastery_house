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
        userId = getIntent().getIntExtra("userId", -1);
        loadUserData();

        setupOrderPanel();
        showMenuScreen("Kawa");

        binding.leftPanelCoffeeBtn.setOnClickListener(v -> showMenuScreen("Kawa"));
        binding.leftPanelTeaBtn.setOnClickListener(v -> showMenuScreen("Herbata"));
        binding.leftPanelDessertsBtn.setOnClickListener(v -> showMenuScreen("Desery"));
        binding.leftPanelAdditivesBtn.setOnClickListener(v -> showMenuScreen("Dodatki"));
        binding.leftPanelSettingsBtn.setOnClickListener(v -> showSettingsScreen());
        binding.rightPanelSummaryBtn.setOnClickListener(v -> showSummaryDialog());
    }

    private void showMenuScreen(String category) {
        FragmentMenuBinding menuBinding =
                FragmentMenuBinding.inflate(getLayoutInflater());
        binding.centerPanel.removeAllViews();
        binding.centerPanel.addView(menuBinding.getRoot());
        menuBinding.categoryTitle.setText(category);

        menuBinding.menuRecycler.setLayoutManager(new GridLayoutManager(this, 3));

        List<MenuItem> items = db.getMenuItemsByCategory(category);

        StandardMenuAdapter adapter = new StandardMenuAdapter(items, item -> {
            onMenuItemClicked(item);
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

        settingsBinding.settingsPageNameInput.setText(currentName);
        settingsBinding.settingsPageSurnameInput.setText(currentSurname);
        settingsBinding.settingsPageEmailInput.setText(currentEmail);
        settingsBinding.settingsPagePasswordInput.setText(currentPassword);

        settingsBinding.settingsScreenCancelChangesButton.setOnClickListener(v -> {
            settingsBinding.settingsPageNameInput.setText(currentName);
            settingsBinding.settingsPageSurnameInput.setText(currentSurname);
            settingsBinding.settingsPageEmailInput.setText(currentEmail);
            settingsBinding.settingsPagePasswordInput.setText(currentPassword);
                });
        settingsBinding.settingsScreenSaveChangesButton.setOnClickListener(v -> {

            String newName = settingsBinding.settingsPageNameInput.getText().toString().trim();
            String newSurname = settingsBinding.settingsPageSurnameInput.getText().toString().trim();
            String newEmail = settingsBinding.settingsPageEmailInput.getText().toString().trim();
            String newPassword = settingsBinding.settingsPagePasswordInput.getText().toString().trim();

            if(newName.isEmpty() || newSurname.isEmpty() || newEmail.isEmpty()){
                Toast.makeText(this, "Wszystkie pola muszą być uzupełnione", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updateBasic = db.updateUserData(userId, newName, newSurname, newEmail);

            boolean updatePassword = false;
            if(!newPassword.isEmpty()) {
                updatePassword = db.updateUserPassword(userId, newPassword);
                currentPassword = newPassword;
            }

            if(updateBasic){
                currentName = newName;
                currentSurname = newSurname;
                currentEmail = newEmail;
            }

            if(updateBasic || updatePassword){
                Toast.makeText(this, "Zmiany zostały zapisane", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wystąpił błąd podczas zapisu", Toast.LENGTH_SHORT).show();
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

        centerPanelParams.weight = centerPanelParams.weight + rightPanelParams.weight;
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
            updateTotalText();
        });
        binding.rightPanelOrderRecycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        binding.rightPanelOrderRecycler.setAdapter(orderAdapter);
    }
        private void onMenuItemClicked(MenuItem item) {
            if ("dessert".equals(item.getType()) || "additive".equals(item.getType())) {
                OrderItem orderItem =
                        new OrderItem(item.getName(), "", item.getPriceSingle());
                addToOrderPanel(orderItem);
            } else {
                showSizeDialog(item);
            }
        }

        private void showSizeDialog(MenuItem item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_choose_size, null);
            builder.setView(view);

            AlertDialog dialog = builder.create();
            dialog.show();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }


            TextView title = dialog.findViewById(R.id.dialog_title);
            android.widget.Button btnS = dialog.findViewById(R.id.btn_size_s);
            android.widget.Button btnM = dialog.findViewById(R.id.btn_size_m);
            android.widget.Button btnL = dialog.findViewById(R.id.btn_size_l);
            android.widget.Button btnCancel = dialog.findViewById(R.id.btn_cancel);

            title.setText(item.getName());

            btnS.setText(String.format("Small - %.2f zł", item.getPriceS()));
            btnM.setText(String.format("Medium - %.2f zł", item.getPriceM()));
            btnL.setText(String.format("Large - %.2f zł", item.getPriceL()));

            btnS.setOnClickListener(v -> {
                addToOrderPanel(new OrderItem(item.getName(), "S", item.getPriceS()));
                dialog.dismiss();
            });

            btnM.setOnClickListener(v -> {
                addToOrderPanel(new com.example.theroasteryhouse.models.OrderItem(item.getName(), "M", item.getPriceM()));
                dialog.dismiss();
            });

            btnL.setOnClickListener(v -> {
                addToOrderPanel(new com.example.theroasteryhouse.models.OrderItem(item.getName(), "L", item.getPriceL()));
                dialog.dismiss();
            });

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

            Toast.makeText(this, "Zamówienie przyjęte!", Toast.LENGTH_LONG).show();

            orderAdapter.clear();
            binding.rightPanelAmountNumber.setText("0.00 zł");

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
