package com.example.theroasteryhouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.theroasteryhouse.databinding.ActivityAdminModeStartBinding;

public class AdminModeStartActivity extends AppCompatActivity {

    private ActivityAdminModeStartBinding binding;
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String KEY_PASSWORD = "admin_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminModeStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_PASSWORD)) {
            prefs.edit().putString(KEY_PASSWORD, "admin").apply();
        }
        binding.adminStartScreenLoginBtn.setOnClickListener(v -> {
            String enteredPassword = binding.etAdminPassword.getText().toString().trim();
            String storedPassword = prefs.getString(KEY_PASSWORD, "admin");

            if (enteredPassword.equals(storedPassword)) {
                Intent intent = new Intent(AdminModeStartActivity.this, AdminModeMainScreenActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Nieprawidłowe hasło!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.adminStartScreenCancelBtn.setOnClickListener(v -> {
            finish();
        });
    }
}