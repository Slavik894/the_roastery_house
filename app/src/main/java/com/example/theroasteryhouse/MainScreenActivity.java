package com.example.theroasteryhouse;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.ActivityMainBinding;
import com.example.theroasteryhouse.databinding.ActivityMainScreenBinding;
import com.example.theroasteryhouse.databinding.FragmentMenuBinding;
import com.example.theroasteryhouse.databinding.FragmentSettingsBinding;

public class MainScreenActivity extends AppCompatActivity {
    private ActivityMainScreenBinding binding;
    private DatabaseHelper db;

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

        showMenuScreen("Kawa");

        binding.leftPanelCoffeeBtn.setOnClickListener(v -> showMenuScreen("Kawa"));
        binding.leftPanelTeaBtn.setOnClickListener(v -> showMenuScreen("Herbata"));
        binding.leftPanelDessertsBtn.setOnClickListener(v -> showMenuScreen("Desery"));
        binding.leftPanelAdditivesBtn.setOnClickListener(v -> showMenuScreen("Dodatki"));
        binding.leftPanelSettingsBtn.setOnClickListener(v -> showSettingsScreen());

    }

    private void showMenuScreen(String category) {
        FragmentMenuBinding menuBinding =
                FragmentMenuBinding.inflate(getLayoutInflater());
        binding.centerPanel.removeAllViews();
        binding.centerPanel.addView(menuBinding.getRoot());
        menuBinding.categoryTitle.setText(category);

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
            String newName = settingsBinding.settingsPageNameInput.getText().toString();
            String newSurname = settingsBinding.settingsPageSurnameInput.getText().toString();
            String newEmail = settingsBinding.settingsPageEmailInput.getText().toString();
            String newPassword = settingsBinding.settingsPagePasswordInput.getText().toString();

            if(newName.isEmpty() || newSurname.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()){
                Toast.makeText(this, "Wszystkie pola muszą być uzupełnione", Toast.LENGTH_SHORT).show();
            }
            boolean isUpdated = db.updateUser(userId, newName, newSurname, newEmail, newPassword);

            if(isUpdated){
                currentName = newName;
                currentSurname = newSurname;
                currentEmail = newEmail;
                currentPassword = newPassword;
                Toast.makeText(this, "Zmiany zostały zapisane", Toast.LENGTH_SHORT).show();
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
}
