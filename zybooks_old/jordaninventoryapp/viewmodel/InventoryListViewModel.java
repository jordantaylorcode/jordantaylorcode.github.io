package com.zybooks.jordaninventoryapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.util.Log;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.zybooks.jordaninventoryapp.model.Item;
import com.zybooks.jordaninventoryapp.repo.InventoryRepository;
import java.util.List;

public class InventoryListViewModel extends AndroidViewModel {

    private final InventoryRepository inventoryRepository;
    private LiveData<List<Item>> allItems;
    private final static String TAG = "InventoryListViewModel";

    public InventoryListViewModel(Application application) {
        super(application);
        inventoryRepository = InventoryRepository.getInstance(application.getApplicationContext());
        allItems = inventoryRepository.getItems();
    }

    public LiveData<List<Item>> getItems() {
        return allItems;
    }

    public void addItem(Item item) {
        Log.d(TAG, "addItem");
        inventoryRepository.addItem(item);
    }

    public Item getItem(long id) {
        return inventoryRepository.getItem(id);
    }

    public void updateItem(Item item) {
        if (item.getQuantity() < 1) {
            String message = item.getName() + "has run out.";
            send_sms("6505551212", message);
        }
        inventoryRepository.updateItem(item);
    }

    public void deleteItem(Item item) {
        Log.d(TAG, "deleteItem");
        new Thread(() -> inventoryRepository.deleteItem(item)).start();
    }

    private void send_sms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(InventoryListViewModel.this.getApplication(), "SMS sent.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(InventoryListViewModel.this.getApplication(), "Could not send SMS.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "" + e.getMessage());
        }
    }
}
