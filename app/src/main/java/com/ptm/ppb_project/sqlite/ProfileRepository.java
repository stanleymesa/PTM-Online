package com.ptm.ppb_project.sqlite;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.ptm.ppb_project.model.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileRepository {
    private ProfileDao profileDao;
    private LiveData<List<ProfileModel>> allProfiles;

    public ProfileRepository(Context context) {
        ProfileDatabase database = ProfileDatabase.getInstance(context);
        profileDao = database.profileDao();
        allProfiles = profileDao.getAllProfiles();
    }

    public void insert(ProfileModel profileModel) {
        new InsertProfileAsyncTask(profileDao).execute(profileModel);
    }

    public void update(ProfileModel profileModel) {
        new UpdateProfileAsyncTask(profileDao).execute(profileModel);
    }

    public void delete(ProfileModel profileModel) {
        new DeleteProfileAsyncTask(profileDao).execute(profileModel);
    }

    public void deleteAllProfiles() {
        new DeleteAllProfileAsyncTask(profileDao).execute();
    }

    public LiveData<List<ProfileModel>> getAllProfiles() {
        return allProfiles;
    }

    public LiveData<ProfileModel> getProfile(String id) {
        return profileDao.getProfile(id);
    }

    private static class InsertProfileAsyncTask extends AsyncTask<ProfileModel, Void, Void> {

        private ProfileDao profileDao;

        public InsertProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ProfileModel... profileModels) {
            profileDao.insert(profileModels[0]);
            return null;
        }
    }

    private static class UpdateProfileAsyncTask extends AsyncTask<ProfileModel, Void, Void> {

        private ProfileDao profileDao;

        public UpdateProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ProfileModel... profileModels) {
            profileDao.update(profileModels[0]);
            return null;
        }
    }

    private static class DeleteProfileAsyncTask extends AsyncTask<ProfileModel, Void, Void> {

        private ProfileDao profileDao;

        public DeleteProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(ProfileModel... profileModels) {
            profileDao.delete(profileModels[0]);
            return null;
        }
    }

    private static class DeleteAllProfileAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProfileDao profileDao;

        public DeleteAllProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            profileDao.deleteAllProfiles();
            return null;
        }
    }

}
