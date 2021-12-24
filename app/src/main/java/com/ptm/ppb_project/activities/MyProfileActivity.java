package com.ptm.ppb_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.model.ProfileModel;
import com.ptm.ppb_project.sqlite.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivProfile;
    MaterialButton btnChoose, btnSave, btnDelete;
    Uri photoProfileUri;
    FirebaseAuth mAuth;
    private String userId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProfileViewModel profileViewModel;
    private boolean isDataEmpty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Hooks
        ivProfile = findViewById(R.id.iv_photoprofile_my);
        btnChoose = findViewById(R.id.btn_choose_image);
        btnSave = findViewById(R.id.btn_save_image);
        btnDelete = findViewById(R.id.btn_delete_image);

        // Set Firebase
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        // Set SQLite
        setSQLite();

        // On Click
        btnChoose.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private void setSQLite() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.getProfile(userId).observe(this, new Observer<ProfileModel>() {
            @Override
            public void onChanged(ProfileModel profileModel) {
                if (profileModel == null) {
                    isDataEmpty = true;
                    photoProfileUri = null;
                    Glide.with(MyProfileActivity.this).load(R.drawable.avatar).into(ivProfile);
                } else {
                    isDataEmpty = false;
                    photoProfileUri = Uri.parse(profileModel.getImage());
                    Glide.with(MyProfileActivity.this).load(Uri.parse(profileModel.getImage())).into(ivProfile);
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photoProfileUri = data.getData();
            Glide.with(this).load(photoProfileUri).into(ivProfile);
        }
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.iv_photoprofile_my) {

        }

        if (btnId == R.id.btn_choose_image) {
            openFileChooser();
        }

        if (btnId == R.id.btn_save_image) {
            if (isDataEmpty) {
                // SAVE
                if (photoProfileUri != null) {
                    ProfileModel profileModel = new ProfileModel(userId, photoProfileUri.toString());
                    profileViewModel.insert(profileModel);
                }
            } else {
                // EDIT
                if (photoProfileUri != null) {
                    ProfileModel profileModel = new ProfileModel(userId, photoProfileUri.toString());
                    profileViewModel.update(profileModel);
                }
            }
            finish();
        }

        if (btnId == R.id.btn_delete_image) {
            if (!isDataEmpty) {
                ProfileModel profileModel = new ProfileModel(userId, photoProfileUri.toString());
                profileViewModel.delete(profileModel);
            }
        }
    }
}