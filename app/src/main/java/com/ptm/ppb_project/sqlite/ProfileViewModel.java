package com.ptm.ppb_project.sqlite;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ptm.ppb_project.model.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    ProfileRepository repository;
    LiveData<List<ProfileModel>> allProfiles;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new ProfileRepository(application.getApplicationContext());
        allProfiles = repository.getAllProfiles();
    }

    public void insert(ProfileModel profileModel) {
        repository.insert(profileModel);
    }

    public void update(ProfileModel profileModel) {
        repository.update(profileModel);
    }

    public void delete(ProfileModel profileModel) {
        repository.delete(profileModel);
    }

    public void deleteAllProfiles() {
        repository.deleteAllProfiles();
    }

    public LiveData<List<ProfileModel>> getAllProfiles() {
        return allProfiles;
    }

    public LiveData<ProfileModel> getProfile(String id) {
        return repository.getProfile(id);
    }
}
