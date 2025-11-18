package com.example.theroasteryhouse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.theroasteryhouse.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DatabaseHelper db;
    private int clickCounter = 0;
    private long lastClickTime = 0;
    private static final long CLICK_TIME_INTERVAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
        db = new DatabaseHelper(this);

        binding.loginButton.setOnClickListener(v -> {
            String email = binding.loginInput.getText().toString();
            String password = binding.passwordInput.getText().toString();


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Proszę wypełnić wszystkie pola.", Toast.LENGTH_SHORT).show();
            }
            boolean isValid = db.checkUserCredentials(email, password);
            if (isValid) {
                int userId = db.getUserId(email, password);
                if (userId != -1) {
                    Toast.makeText(this, "Zalogowano", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, SelectOrderTypeActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Nieprawidłowe dane logowania", Toast.LENGTH_SHORT).show();
                }
            }

        });

        binding.registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);

        });

        binding.pageLogo.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();

            if(clickCounter ==0 || (currentTime - lastClickTime > CLICK_TIME_INTERVAL)){
                lastClickTime = currentTime;
                clickCounter = 1;
            } else {
                clickCounter++;
            }
            if(clickCounter == 5) {
                Toast.makeText(this, "Witaj w trybie admina!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), AdminModeStartActivity.class);
                startActivity(intent);
            }
        });


        }
    }

