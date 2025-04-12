package com.zybooks.jordaninventoryapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zybooks.jordaninventoryapp.model.Item;
import com.zybooks.jordaninventoryapp.viewmodel.GridAdapter;
import com.zybooks.jordaninventoryapp.viewmodel.InventoryListViewModel;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private GridAdapter gridAdapter;
    private RecyclerView recyclerView;
    private EditText searchText;
    private InventoryListViewModel itemViewModel;
    private final static String TAG = "MainActivity";
    private static final int REQUEST_CODE_EDIT_ITEM = 1;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)) {
            Log.d(TAG, "has SMS permission");
        } else {
            Log.d(TAG, "does not have SMS permission");
        }

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // logging login status
        String message = (isLoggedIn) ? "logged in" : "not logged in";
        Log.d(TAG, message);

        handleLogin(isLoggedIn);

        recyclerView = findViewById(R.id.item_recycler_view);

        // layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        // view model
        itemViewModel = new ViewModelProvider(this).get(InventoryListViewModel.class);

        // grid adapter
        gridAdapter = new GridAdapter(this, itemViewModel);
        recyclerView.setAdapter(gridAdapter);

        // observing live data
        itemViewModel.getItems().observe(this, items -> gridAdapter.setItems(items));

        // search text and respond to changes in search text
        searchText = findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // when the text changes
                List<Item> filteredList = gridAdapter.getItemList();
                filteredList = filteredList.stream()
                        .filter(item -> s.toString().toLowerCase(Locale.US).isEmpty() || item.getName().toLowerCase().contains(s.toString()))
                        .collect(Collectors.toList());
                gridAdapter.setFilteredList(filteredList);
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // before the text changes
            }

            @Override
            public void afterTextChanged(Editable e) {
                // after the text changes
            }
        });
    }

    private void handleLogin(boolean isLoggedIn) {
        if (isLoggedIn) {
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inventoryItems), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
        addItemResult.launch(intent);
    }

    ActivityResultLauncher<Intent> addItemResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == MainActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String name = data.getStringExtra("name");
                            Log.d(TAG, "ActivityResultLauncher " + name);

                            int quantity = data.getIntExtra("quantity", 0);
                            Log.d(TAG, "ActivityResultLauncher " + String.valueOf(quantity));

                            String description = data.getStringExtra("description");
                            Log.d(TAG, "ActivityResultLauncher " + description);

                            if (!name.isEmpty() && !description.isEmpty()) {
                                Item item = new Item();
                                item.setName(name);
                                item.setQuantity(quantity);
                                item.setDescription(description);
                                Log.d(TAG, "ActivityResultLauncher " + name + " " + description);
                                itemViewModel.addItem(item);
                            } else {
                                Toast.makeText(MainActivity.this, "Problem adding item.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
    );

    @Override // for update item in GridAdapter.java
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_ITEM && resultCode == RESULT_OK) {
            long itemId = data.getLongExtra("id", -1);
            String name = data.getStringExtra("name");
            String description = data.getStringExtra("description");
            int quantity = data.getIntExtra("quantity", 0);

            Item updateItem = new Item();
            updateItem.setId(itemId);
            updateItem.setQuantity(quantity);
            updateItem.setName(name);
            updateItem.setDescription(description);
            if (description == null) {
                description = "";
            }
            if (name == null) {
                name = "";
            }

            updateItem.setName(name);
            updateItem.setDescription(description);
            updateItem.setQuantity(quantity);

            itemViewModel.updateItem(updateItem);
            gridAdapter.notifyDataSetChanged();
        }
    }

    public void logOutClick(View view) {
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void settingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}