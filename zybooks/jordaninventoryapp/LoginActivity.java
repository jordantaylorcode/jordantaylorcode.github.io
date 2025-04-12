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
import com.zybooks.jordaninventoryapp.model.ApiResponse;
import com.zybooks.jordaninventoryapp.model.User;
import com.zybooks.jordaninventoryapp.repo.HTTPService;
import com.zybooks.jordaninventoryapp.repo.InventoryRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private static final String EXTRA_USER_ID = "com.zybooks.jordaninventoryapp.user_id";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private long userId;
    private User user;
    private Button login;
    private Button signUp;
    private HTTPService repository;
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
        repository = InventoryRepository.getInstance().getService();
    }

    public void loginClick(View view) {
        Log.d(TAG, "login clicked");
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        Log.d(TAG, "user object created");

        Call<ApiResponse> call = repository.checkUserExists(user);
        Log.d(TAG, "checkUserExists(user)");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isLogin()) {
                    Log.d(TAG, "Login response: " + response.body().getStatus());
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    // start the main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(TAG, "SignUp response error: " + response.message());
                    Toast.makeText(LoginActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void signUpClick(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!(username.isEmpty() || password.isEmpty())) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            Log.d(TAG, "user object set");

            // set up post request for user sign up
            Call<ApiResponse> call = repository.createUser(user);
            Log.d(TAG, "repository.createUser(user)");

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Log.d(TAG, "onResponse");
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "SignUp response: " + response.body().getStatus());
                        String status = response.body().getStatus();
                        Toast.makeText(LoginActivity.this, "Login: " + status, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "SignUp response error: " + response.message());
                        Toast.makeText(LoginActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e(TAG, "Error: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                }
            });

            usernameEditText.setText("");
            passwordEditText.setText("");
        }
    }

}