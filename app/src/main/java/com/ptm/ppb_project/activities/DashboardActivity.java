package com.ptm.ppb_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.model.ProfileModel;
import com.ptm.ppb_project.model.UserModel;
import com.ptm.ppb_project.session.SessionManager;
import com.ptm.ppb_project.sqlite.ProfileViewModel;

import java.util.List;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    SessionManager loginSession, rememberMeSession;
    MaterialButton btnLogout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView ivMenu, ivClose, ivProfile;
    TextView tvNama;
    View viewDrawer;
    RelativeLayout layoutMatematika, layoutFisika, layoutBiologi, layoutKimia;
    FloatingActionButton fab;
    FirebaseAuth mAuth;
    private String userId;

    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Hooks
        btnLogout = findViewById(R.id.btn_logout);
        drawerLayout = findViewById(R.id.layout_drawer);
        navigationView = findViewById(R.id.navigation_view);
        ivMenu = findViewById(R.id.btn_menu_dashboard);
        ivClose = findViewById(R.id.btn_close_dashboard);
        layoutMatematika = findViewById(R.id.layout_matematika);
        layoutFisika = findViewById(R.id.layout_fisika);
        layoutBiologi = findViewById(R.id.layout_biologi);
        layoutKimia = findViewById(R.id.layout_kimia);
        fab = findViewById(R.id.fab);

        // Session
        loginSession = new SessionManager(this, SessionManager.LOGIN_SESSION);
        rememberMeSession = new SessionManager(this, SessionManager.REMEMBERME_SESSION);

        // Set Firebase
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        // On Click
        btnLogout.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        layoutMatematika.setOnClickListener(this);
        layoutFisika.setOnClickListener(this);
        layoutBiologi.setOnClickListener(this);
        layoutKimia.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set Navigation Drawer
        setNavDrawer();
    }

    private void setNavDrawer() {

        navigationView.bringToFront();
        viewDrawer = navigationView.getHeaderView(0);

        // Hooks
        tvNama = viewDrawer.findViewById(R.id.tv_nama_navdrawer);
        ivProfile = viewDrawer.findViewById(R.id.iv_profilephoto_nav);


        // Settings
        setSQLite();
        tvNama.setText(loginSession.getLoginSessionData().getFullname());
        navigationView.setCheckedItem(R.id.menu_home);
        navigationView.setItemTextColor(getColorState());
        navigationView.setItemIconTintList(getColorState());

        // Onclick
        ivProfile.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_my_schedule) {
                    navigationView.setCheckedItem(item);
                    startActivity(new Intent(getBaseContext(), CartActivity.class));
                }

                return true;
            }
        });
    }

    private void setSQLite() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.getProfile(userId).observe(this, new Observer<ProfileModel>() {
            @Override
            public void onChanged(ProfileModel profileModel) {
                if (profileModel == null) {
                    Glide.with(DashboardActivity.this).load(R.drawable.avatar).into(ivProfile);
                } else {
                    Glide.with(DashboardActivity.this).load(Uri.parse(profileModel.getImage())).into(ivProfile);
                }
            }
        });
    }

    private ColorStateList getColorState() {
        int[][] state = new int[][] {
                new int[] {android.R.attr.state_checked},
                new int[] {-android.R.attr.state_pressed},
                new int[] {android.R.attr.state_pressed}

        };

        int[] color = new int[] {
                Color.WHITE,
                getResources().getColor(R.color.navy),
                Color.WHITE
        };

        return new ColorStateList(state, color);
    }


    private void goToPelajaran(String from) {
        Intent intent = new Intent(this, PelajaranActivity.class);
        intent.putExtra("from", from);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_logout) {
            rememberMeSession.clearRememberMeSession();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (btnId == R.id.btn_close_dashboard) {
            MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("Close Application")
                    .setCancelable(true)
                    .setMessage("Apakah anda yakin ingin keluar?")
                    .setIcon(R.drawable.ic_baseline_directions_run_24)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    });
            alertDialog.show();
        }

        if (btnId == R.id.btn_menu_dashboard) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

        if (btnId == R.id.layout_matematika) {
            goToPelajaran("Matematika");
        }

        if (btnId == R.id.layout_fisika) {
            goToPelajaran("Fisika");
        }

        if (btnId == R.id.layout_biologi) {
            goToPelajaran("Biologi");
        }

        if (btnId == R.id.layout_kimia) {
            goToPelajaran("Kimia");
        }

        if (btnId == R.id.fab) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        }

        if (btnId == R.id.iv_profilephoto_nav) {
            Intent intent = new Intent(this, MyProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}