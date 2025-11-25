package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theroasteryhouse.databinding.ActivityAdminModeMainScreenBinding;
import com.example.theroasteryhouse.databinding.FragmentAdminUserRegisterBinding;
import com.example.theroasteryhouse.databinding.FragmentMenuBinding;
import com.example.theroasteryhouse.databinding.FragmentAdminEditUserDataBinding;
import com.example.theroasteryhouse.models.User;

import java.util.List;

public class AdminModeMainScreenActivity extends AppCompatActivity {

    private ActivityAdminModeMainScreenBinding binding;
    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminModeMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);

        showUsersScreen();
        binding.adminLeftPanelUsersBtn.setOnClickListener(v -> showUsersScreen());
        binding.adminLeftPanelMenuItemsBtn.setOnClickListener(v -> showMenuItemsScreen());
        binding.adminLeftPanelIngredientsBtn.setOnClickListener(v -> showMenuScreen("Składniki"));
        binding.adminLeftPanelFinancesBtn.setOnClickListener(v -> showMenuScreen("Finanse"));
        binding.adminLeftPanelExitBtn.setOnClickListener(view -> finish());
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

    private void showUsersScreen() {
        View usersView = getLayoutInflater()
                .inflate(R.layout.fragment_admin_users, binding.centerPanel, false);

        binding.centerPanel.removeAllViews();
        binding.centerPanel.addView(usersView);

        RecyclerView recyclerView = usersView.findViewById(R.id.admin_mode_users_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersView.findViewById(R.id.admin_mode_users_screen_add_new_user_button)
                .setOnClickListener(v -> showRegisterScreen());

        List<User> users = db.getAllUsers();

        AdminUsersAdapter adapter = new AdminUsersAdapter(
                users,
                new AdminUsersAdapter.OnUserActionListener() {

                    public void onEdit(User user) {

                        showEditUserScreen(user);
                    }

                    @Override
                    public void onDelete(User user) {
                        db.deleteUser(user.getId());
                        showUsersScreen();
                    }
                });

        recyclerView.setAdapter(adapter);
    }

    private void showRegisterScreen() {
        FragmentAdminUserRegisterBinding registerBinding =
                FragmentAdminUserRegisterBinding.inflate(getLayoutInflater());

        binding.centerPanel.removeAllViews();
        binding.centerPanel.addView(registerBinding.getRoot());

        registerBinding.adminUserRegisterScreenRegisterButton.setOnClickListener(v -> {
            String firstName = registerBinding.adminUserRegisterScreenNameInput.getText().toString().trim();
            String lastName = registerBinding.adminUserRegisterScreenSurnameInput.getText().toString().trim();
            String email = registerBinding.adminUserRegisterScreenEmailInput.getText().toString().trim();
            String password = registerBinding.adminUserRegisterScreenPasswordInput.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(AdminModeMainScreenActivity.this, "Proszę wypełnić wszystkie pola.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.userExists(email)) {
                Toast.makeText(AdminModeMainScreenActivity.this, "Użytkownik o podanym adresie e-mail już istnieje.", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = db.addUser(firstName, lastName, email, password);

            if (result == -1) {
                Toast.makeText(this, "Błąd podczas rejestracji", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Rejestracja zakończona sukcesem", Toast.LENGTH_SHORT).show();
                showUsersScreen();
            }
        });
    }

    private void showEditUserScreen(User user) {
        FragmentAdminEditUserDataBinding editBinding =
                FragmentAdminEditUserDataBinding.inflate(getLayoutInflater());

        binding.centerPanel.removeAllViews();
        binding.centerPanel.addView(editBinding.getRoot());

        editBinding.adminEditDataScreenNameInput.setText(user.getFirstName());
        editBinding.adminEditDataScreenSurnameInput.setText(user.getLastName());
        editBinding.adminEditDataScreenEmailInput.setText(user.getEmail());

        editBinding.adminEditDataScreenPasswordInput.setEnabled(false);
        editBinding.adminEditDataScreenPasswordInput.setFocusable(false);
        editBinding.adminEditDataScreenPasswordInput.setFocusableInTouchMode(false);

        editBinding.adminEditDataScreenResetPasswordButton.setOnClickListener(v -> {
            editBinding.adminEditDataScreenPasswordInput.setEnabled(true);
            editBinding.adminEditDataScreenPasswordInput.setFocusableInTouchMode(true);
            editBinding.adminEditDataScreenPasswordInput.requestFocus();

            android.widget.Toast.makeText(
                    this,
                    "Wprowadź nowe hasło użytkownika",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
        });

        editBinding.adminEditDataScreenSaveChangesButton.setOnClickListener(v -> {
            String newName = editBinding.adminEditDataScreenNameInput.getText().toString().trim();
            String newSurname = editBinding.adminEditDataScreenSurnameInput.getText().toString().trim();
            String newEmail = editBinding.adminEditDataScreenEmailInput.getText().toString().trim();
            String newPassword = editBinding.adminEditDataScreenPasswordInput.getText().toString().trim();

            db.updateUserData(user.getId(), newName, newSurname, newEmail);


            if (editBinding.adminEditDataScreenPasswordInput.isEnabled()
                    && !newPassword.isEmpty()) {
                db.updateUserPassword(user.getId(), newPassword);
            }

            android.widget.Toast.makeText(
                    this,
                    "Dane użytkownika zapisane",
                    android.widget.Toast.LENGTH_SHORT
            ).show();


            showUsersScreen();
        });


        editBinding.adminEditDataScreenDiscardChangesButton.setOnClickListener(v -> {
            showUsersScreen();
        });

        editBinding.adminEditDataScreenDeleteUserButton.setOnClickListener(v -> {
            db.deleteUser(user.getId());
            android.widget.Toast.makeText(
                    this,
                    "Użytkownik usunięty",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
            showUsersScreen();
        });
    }

    private void showMenuItemsScreen() {
        binding.centerPanel.removeAllViews();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.center_panel, new AdminMenuItemsFragment())
                .commit();
    }



}

