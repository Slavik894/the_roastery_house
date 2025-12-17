package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.theroasteryhouse.databinding.ActivitySpecialModeMainScreenBinding;

public class SpecialModeMainScreenActivity extends AppCompatActivity {

    private ActivitySpecialModeMainScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpecialModeMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
}