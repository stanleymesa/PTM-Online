package com.ptm.ppb_project.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.activities.LoginActivity;
import com.ptm.ppb_project.adapter.TodayLessonsAdapter;
import com.ptm.ppb_project.model.PelajaranModel;
import com.ptm.ppb_project.session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity implements View.OnClickListener, TodayLessonsAdapter.OnShowMoreClickback {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView tvNama, tvTime, tvTotalStudents, tvTotalLessons;
    SessionManager loginSession, rememberMeSession;
    ImageView btnMenu, ivClose;
    View viewDrawer;
    String currentTime;
    MaterialButton btnLogout;
    FirebaseFirestore firestoreRoot;
    RecyclerView rvTodayLessons;
    DocumentSnapshot lastVisible;
    ArrayList<PelajaranModel> listPelajaran = new ArrayList<>();
    TodayLessonsAdapter adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Hooks
        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.navigation_view_admin);
        btnMenu = findViewById(R.id.btn_menu_dashboard_admin);
        btnLogout = findViewById(R.id.btn_logout_admin);
        tvTime = findViewById(R.id.tv_time_admin);
        tvTotalStudents = findViewById(R.id.tv_total_students);
        tvTotalLessons = findViewById(R.id.tv_total_lessons);
        rvTodayLessons = findViewById(R.id.rv_today_lessons);
        ivClose = findViewById(R.id.iv_close_dashboard_admin);
        progressBar = findViewById(R.id.pb_admindashboard);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();

        // Set Session
        loginSession = new SessionManager(this, SessionManager.LOGIN_SESSION);
        rememberMeSession = new SessionManager(this, SessionManager.REMEMBERME_SESSION);

        // On Click
        btnMenu.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        ivClose.setOnClickListener(this);

        setSnackbar("Hi, " + loginSession.getLoginSessionData().getFullname() + "!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNavDrawer();
        setAdminArea();
        setInitialTodayLessons();
    }

    private void setSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.content), text, Snackbar.LENGTH_SHORT)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setBackgroundTint(getResources().getColor(R.color.darknavy))
                .setActionTextColor(getResources().getColor(R.color.white));
        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        TextView actionText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
        snackbarText.setTypeface(ResourcesCompat.getFont(this, R.font.quicksand_medium));
        actionText.setTypeface(ResourcesCompat.getFont(this, R.font.quicksand_bold));
        snackbar.show();
    }

    private void setInitialTodayLessons() {
        progressBar.setVisibility(View.VISIBLE);
        listPelajaran.clear();
        rvTodayLessons.setLayoutManager(new LinearLayoutManager(this));

        firestoreRoot.collection("pelajaran")
                .orderBy("kelas", Query.Direction.ASCENDING)
                .orderBy("start_at", Query.Direction.ASCENDING)
                .whereEqualTo("hari", getCurrentHari())
                .limit(3)
                .get()
                .addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            listPelajaran.add(ds.toObject(PelajaranModel.class));
                        }
                        adapter = new TodayLessonsAdapter(listPelajaran, AdminDashboardActivity.this);
                        rvTodayLessons.setAdapter(adapter);
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setTodayLessons() {
        progressBar.setVisibility(View.VISIBLE);
        Query query = firestoreRoot.collection("pelajaran")
                .orderBy("kelas", Query.Direction.ASCENDING)
                .orderBy("start_at", Query.Direction.ASCENDING)
                .whereEqualTo("hari", getCurrentHari())
                .startAfter(lastVisible)
                .limit(3);

        query.get()
                .addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            listPelajaran.add(ds.toObject(PelajaranModel.class));
                        }
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setAdminArea() {
        // Set Current Time
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        currentTime = formatter.format(System.currentTimeMillis());
        tvTime.setText(currentTime);
        // End Set

        // Set Registered
        firestoreRoot.document("stats/qty")
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null) {
                            tvTotalStudents.setText(String.valueOf(value.getLong("user")));
                            tvTotalLessons.setText(String.valueOf(value.getLong("pelajaran")));
                        }
                    }

                });
        // End Set

    }

    private String getCurrentHari() {
        String day = currentTime.substring(0, currentTime.indexOf(","));

        switch (day) {
            case "Monday":
                return "Senin";
            case "Tuesday":
                return "Selasa";
            case "Wednesday":
                return "Rabu";
            case "Thursday":
                return "Kamis";
            case "Friday":
                return "Jumat";
            case "Saturday":
                return "Sabtu";
            default:
                return "Minggu";
        }
    }

    private void setNavDrawer() {

        navigationView.bringToFront();
        viewDrawer = navigationView.getHeaderView(0);
        tvNama = viewDrawer.findViewById(R.id.tv_nama_navdrawer_admin);
        tvNama.setText(loginSession.getLoginSessionData().getFullname());

        navigationView.setCheckedItem(R.id.menu_home);

        navigationView.setItemTextColor(getColorState());
        navigationView.setItemIconTintList(getColorState());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_lessons) {
                    navigationView.setCheckedItem(item);
                    startActivity(new Intent(getBaseContext(), SearchLessonsActivity.class));
                }

                return true;
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

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_menu_dashboard_admin) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

        if (btnId == R.id.btn_logout_admin) {
            rememberMeSession.clearRememberMeSession();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (btnId == R.id.iv_close_dashboard_admin) {
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
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onShowMoreClicked(TextView tvShowMore) {
        setTodayLessons();
        tvShowMore.setVisibility(View.GONE);
    }
}