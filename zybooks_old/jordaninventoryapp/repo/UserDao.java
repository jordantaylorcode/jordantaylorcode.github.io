package com.zybooks.jordaninventoryapp.repo;

import androidx.room.*;
import com.zybooks.jordaninventoryapp.model.User;
import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM User WHERE id = :id")
    User getUser(long id);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long addUser(User user);

    // query to check if user exists by username
    @Query("SELECT * FROM User WHERE username = :username AND password = :password LIMIT 1")
    User checkUserExists(String username, String password);
}
