package com.ptm.ppb_project.sqlite;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ptm.ppb_project.model.ProfileModel;

@Database(entities = {ProfileModel.class}, version = 2)
public abstract class ProfileDatabase extends RoomDatabase {
    private static ProfileDatabase instance;
    public abstract ProfileDao profileDao();

    public static synchronized ProfileDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ProfileDatabase.class, "profile_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
