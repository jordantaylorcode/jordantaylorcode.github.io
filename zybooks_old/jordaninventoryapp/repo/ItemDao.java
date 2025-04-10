package com.zybooks.jordaninventoryapp.repo;

import androidx.room.*;
import androidx.lifecycle.LiveData;
import com.zybooks.jordaninventoryapp.model.Item;
import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM Item")
    LiveData<List<Item>> getItems();

    @Query(("SELECT * FROM Item WHERE id = :id"))
    Item getItem(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addItem(Item item);

    @Update
    void updateItem(Item item);

    @Delete
    void deleteItem(Item item);
}
