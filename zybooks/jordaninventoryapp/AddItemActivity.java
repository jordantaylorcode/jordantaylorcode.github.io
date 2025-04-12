package com.zybooks.jordaninventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddItemActivity extends AppCompatActivity {

    private EditText name;
    private EditText quantity;
    private EditText description;
    private final static String TAG = "AddItemActivity";
    private long itemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.nameEdit);
        quantity = findViewById(R.id.quantityEdit);
        description = findViewById(R.id.descriptionEdit);

        itemId = getIntent().getLongExtra("id", -1);

        if (itemId != -1) {
            String i_name = getIntent().getStringExtra("name");
            String i_description = getIntent().getStringExtra("description");
            String i_quantity = getIntent().getStringExtra("quantity");

            name.setText(i_name);
            description.setText(i_description);
            quantity.setText(i_quantity);
        }
    }

    public void okClick(View view) {
        Log.d(TAG, "okClick");
        Intent intent = new Intent();

        intent.putExtra("id", itemId);

        intent.putExtra("name", name.getText().toString().trim());
        Log.d(TAG, "putExtra - " + name.getText().toString().trim());

        intent.putExtra("quantity", Integer.parseInt(quantity.getText().toString().trim()));
        Log.d(TAG, "putExtra - " + quantity.getText().toString().trim());

        intent.putExtra("description", description.getText().toString().trim());
        Log.d(TAG, "putExtra - " + description.getText().toString().trim());

        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelClick(View view) {
        finish();
    }
}