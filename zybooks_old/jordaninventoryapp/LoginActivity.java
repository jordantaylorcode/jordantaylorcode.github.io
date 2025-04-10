package com.zybooks.jordaninventoryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.zybooks.jordaninventoryapp.model.User;
import com.zybooks.jordaninventoryapp.repo.InventoryRepository;

public class LoginActivity extends AppCompatActivity {

    private static final String EXTRA_USER_ID = "com.zybooks.jordaninventoryapp.user_id";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private long userId;
    private User user;
    private Button login;
    private Button signUp;
    private InventoryRepository repository;
    private final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.usernameEdit);
        passwordEditText = findViewById(R.id.passwordEdit);
        login = findViewById(R.id.button);
        signUp = findViewById(R.id.button2);
        repository = InventoryRepository.getInstance(getApplicationContext());
    }

    public void loginClick(View view) {
        Log.d(TAG, "login clicked");
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        User user = repository.checkUserExists(username, password);

        if (user != null) {
            Log.i(TAG, user.getUsername() + " logged in");
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Username or password incorrect.", Toast.LENGTH_SHORT).show();
        }

    }

    public void signUpClick(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!(username.isEmpty() || password.isEmpty())) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            Log.d(TAG, "user ID is: " + String.valueOf(repository.addUser(user)));

            usernameEditText.setText("");
            passwordEditText.setText("");
            // need to figure out why password user isn't getting added
        }
    }

}