package com.zybooks.jordaninventoryapp.repo;

import android.content.Context;

import androidx.room.Room;

import com.zybooks.jordaninventoryapp.model.Item;
import com.zybooks.jordaninventoryapp.model.User;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryRepository {

    private static InventoryRepository inventoryRepo;
    private final UserDao userDao;
    private final ItemDao itemDao;
    private final static String TAG = "Database";

    public static InventoryRepository getInstance(Context context) {
        if (inventoryRepo == null) {
            inventoryRepo = new InventoryRepository(context);
        }
        return inventoryRepo;
    }

    private InventoryRepository(Context context) {
        Log.d(TAG, "database instantiated.");
        InventoryDatabase database = Room.databaseBuilder(context, InventoryDatabase.class, "inventory.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        userDao = database.userDao();
        itemDao = database.itemDao();
    }

    // user getters and setters
    public User getUser(long userId) {
        return userDao.getUser(userId);
    }

    public User checkUserExists(String username, String password) {
        return userDao.checkUserExists(username, password);
    }

    public long addUser(User user) {
        return userDao.addUser(user);
    }

    // item getters and setters
    public LiveData<List<Item>> getItems() {
        return itemDao.getItems();
    }

    public long addItem(Item item) {
        Log.d(TAG, "addItem");
        return itemDao.addItem(item);
    }

    public Item getItem(long id) {
        return itemDao.getItem(id);
    }

    public void updateItem(Item item) {
        itemDao.updateItem(item);
    }

    public void deleteItem(Item item) {
        Log.d(TAG, "deleteItem");
        itemDao.deleteItem(item);
    }
}
