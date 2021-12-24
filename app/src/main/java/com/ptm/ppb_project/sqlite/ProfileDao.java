package com.ptm.ppb_project.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ptm.ppb_project.model.ProfileModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ProfileDao {
    @Insert
    void insert(ProfileModel profile);

    @Update
    void update(ProfileModel profile);

    @Delete
    void delete(ProfileModel profile);

    @Query("DELETE FROM profile_table")
    void deleteAllProfiles();

    @Query("SELECT * FROM profile_table")
    LiveData<List<ProfileModel>>
    getAllProfiles();

    @Query("SELECT * FROM profile_table WHERE id = :id")
    LiveData<ProfileModel> getProfile(String id);
}
