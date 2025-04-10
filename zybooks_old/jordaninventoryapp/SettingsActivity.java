package com.zybooks.jordaninventoryapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SMS_PERMISSION = 1;
    private static final String TAG = "SettingsActivity";
    private static boolean permission_granted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsFragment fragment = new SettingsFragment();
        fragment.accessOuter(this);
        Log.d(TAG, "onCreate PRE");
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, fragment)
                    .commitNow();
        }

        if (!permission_granted) {
            requestPermission();
        } else {
            enableSmsNotifications();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Log.d(TAG, "onCreate POST");
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Activity outer;
        public void accessOuter(SettingsActivity activity) {
            outer = activity;
        }
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Log.d(TAG, "onCreatePreferences");
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreferenceCompat sms_notifications = findPreference("sync");
            Log.d(TAG, "found preference");
            if (sms_notifications != null) {
                sms_notifications.setOnPreferenceChangeListener((preference, newValue) -> {
                    if ((Boolean) newValue) {
                        String sms_permission = Manifest.permission.SEND_SMS;
                        SettingsActivity activity = new SettingsActivity();
                        Log.d(TAG, "new settings activity instance");
                        if (ContextCompat.checkSelfPermission(outer, sms_permission) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "request permission PRE");
                            permission_granted = false;
                            Log.d(TAG, "request permission POST");
                        } else {
                            Log.d(TAG, "enable sms PRE");
                            permission_granted = true;
                        }
                    } else {
                        Log.d(TAG, "turned off");
                    }
                    return true;
                });
            }

        }
    }

    private void requestPermission() {
        Log.d(TAG, "requestPermission() pre");
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "SMS permission is needed to send notifications.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d(TAG, "req perm " + e.getMessage());
        }

        Log.d(TAG, "requestPermission() post");
        //request the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_SMS_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);

        if (requestCode == REQUEST_CODE_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableSmsNotifications();
            } else {
                Toast.makeText(this, "SMS permission is required to send notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableSmsNotifications() {
        Toast.makeText(this, "SMS notifications enabled.", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("smsNotifications", true);
        editor.apply();
    }
}