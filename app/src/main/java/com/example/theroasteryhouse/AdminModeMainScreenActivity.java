package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.theroasteryhouse.databinding.ActivityAdminModeMainScreenBinding;
import com.example.theroasteryhouse.databinding.ActivityAdminModeStartBinding;
import com.example.theroasteryhouse.databinding.FragmentMenuBinding;

public class AdminModeMainScreenActivity extends AppCompatActivity {

    private ActivityAdminModeMainScreenBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminModeMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showMenuScreen("Użytkownicy");

        binding.adminLeftPanelUsersBtn.setOnClickListener(v -> showMenuScreen("Użytkownicy"));
        binding.adminLeftPanelMenuItemsBtn.setOnClickListener(v -> showMenuScreen("Pozycje menu"));
        binding.adminLeftPanelIngredientsBtn.setOnClickListener(v -> showMenuScreen("Składniki"));
        binding.adminLeftPanelFinancesBtn.setOnClickListener(v -> showMenuScreen("Finanse"));
    }

    private void showMenuScreen(String category) {
        FragmentMenuBinding menuBinding =
                FragmentMenuBinding.inflate(getLayoutInflater());
        binding.centerPanel.removeAllViews();
        binding.centerPanel.addView(menuBinding.getRoot());
        menuBinding.categoryTitle.setText(category);

        binding.adminLeftPanelExitBtn.setOnClickListener(view -> {
            finish();
        });

    }



    }

