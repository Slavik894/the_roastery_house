package com.example.theroasteryhouse;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.theroasteryhouse.databinding.ActivityMainBinding;
import com.example.theroasteryhouse.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new DatabaseHelper(this);

            binding.registerPageRegisterButton.setOnClickListener(v -> {
                String firstName = binding.registerPageNameInput.getText().toString();
                String lastName = binding.registerPageSurnameInput.getText().toString();
                String email = binding.registerPageEmailInput.getText().toString();
                String password = binding.registerPagePasswordInput.getText().toString();

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Proszę wypełnić wszystkie pola.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(db.userExists(email)) {
                    Toast.makeText(RegisterActivity.this, "Użytkownik o podanym adresie e-mail już istnieje.", Toast.LENGTH_SHORT).show();
                    return;
                }
                long result = db.addUser(firstName, lastName, email, password);
                if (result == -1) {
                    Toast.makeText(this, "Błąd podczas rejestracji", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Rejestracja zakończona sukcesem", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
    }
}
