package com.example.theroasteryhouse;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.theroasteryhouse.databinding.ActivityMainBinding;
import com.example.theroasteryhouse.databinding.ActivitySelectOrderTypeBinding;

public class SelectOrderTypeActivity extends AppCompatActivity {
    private ActivitySelectOrderTypeBinding binding;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectOrderTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        userId = getIntent().getIntExtra("userId", -1);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.standardOrderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainScreenActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);

        });



    }
}