package com.zybooks.jordaninventoryapp.repo;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.zybooks.jordaninventoryapp.model.User;
import com.zybooks.jordaninventoryapp.model.Item;

@Database(entities = {User.class, Item.class}, version = 3)
public abstract class InventoryDatabase  extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ItemDao itemDao();
}
