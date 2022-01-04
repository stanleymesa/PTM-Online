package com.ptm.ppb_project.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.adapter.PelajaranAdapter;
import com.ptm.ppb_project.model.CartModel;
import com.ptm.ppb_project.model.PelajaranModel;
import com.ptm.ppb_project.session.SessionManager;

import java.util.ArrayList;

public class PelajaranActivity extends AppCompatActivity implements PelajaranAdapter.OnItemClickCallback, View.OnClickListener {

    ImageView ivBack;
    RecyclerView rv;
    FirebaseFirestore firestoreRoot;
    FirebaseAuth mAuth;
    SessionManager loginSession;
    PelajaranAdapter adapter;
    String uid;
    ProgressBar progressBar;
    TextView tvTitle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelajaran);

        // Hooks
        ivBack = findViewById(R.id.iv_back_pelajaran);
        rv = findViewById(R.id.rv_pelajaran);
        progressBar = findViewById(R.id.pb_pelajaran);
        tvTitle = findViewById(R.id.tv_title_pelajaran);



        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        assert  mAuth.getCurrentUser() != null;
        uid = mAuth.getCurrentUser().getUid();

        // On Click
        ivBack.setOnClickListener(this);

        // Set Session
        loginSession = new SessionManager(this, SessionManager.LOGIN_SESSION);

        String fromWhere = getIntent().getStringExtra("from");
        if (fromWhere.equals("Matematika")) {
            setPelajaranAdapter(fromWhere);
        }
        else if (fromWhere.equals("Fisika")) {
            setPelajaranAdapter(fromWhere);
        }
        else if (fromWhere.equals("Biologi")) {
            setPelajaranAdapter(fromWhere);
        }
        else if (fromWhere.equals("Kimia")){
            setPelajaranAdapter(fromWhere);
        }

        // Set Title
        tvTitle.setText(fromWhere + " Kelas " + loginSession.getLoginSessionData().getKelas() );

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


    private void setPelajaranAdapter(String matpel) {

        progressBar.setVisibility(View.VISIBLE);
        String kelas = loginSession.getLoginSessionData().getKelas();
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        firestoreRoot.collection("pelajaran")
                .orderBy("start_at", Query.Direction.ASCENDING)
                .whereEqualTo("matpel", matpel)
                .whereEqualTo("kelas", kelas)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {

                        if (error != null) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        // Jika Pelajaran Ada
                        if (value != null && !value.isEmpty()) {
                            ArrayList<PelajaranModel> dataPelajaran = new ArrayList<>();
                            for (DocumentSnapshot ds : value) {
                                dataPelajaran.add(ds.toObject(PelajaranModel.class));
                            }


                            // CEK CART
                            firestoreRoot.collection("carts/CART_" + uid + "/items")
                                    .addSnapshotListener(PelajaranActivity.this, new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {

                                            if (error != null) {
                                                progressBar.setVisibility(View.GONE);
                                                return;
                                            }

                                            // Jika Cart Ada
                                            if (value != null && !value.isEmpty()) {
                                                ArrayList<String> dataId = new ArrayList<>();
                                                for (DocumentSnapshot ds : value) {
                                                    dataId.add(ds.getId());
                                                }


                                                firestoreRoot.collection("pelajaran").whereIn(FieldPath.documentId(), dataId).get()
                                                        .addOnSuccessListener(PelajaranActivity.this, new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                // Jika ada pelajarannya
                                                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                                                    ArrayList<PelajaranModel> dataCart = new ArrayList<>();
                                                                    for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                                                        dataCart.add(ds.toObject(PelajaranModel.class));
                                                                    }
                                                                    adapter = new PelajaranAdapter(PelajaranActivity.this, dataPelajaran, dataCart);
                                                                }

                                                                // Jika tidak ada pelajarannya
                                                                else {
                                                                    ArrayList<PelajaranModel> dataCart = new ArrayList<>();
                                                                    adapter = new PelajaranAdapter(PelajaranActivity.this, dataPelajaran, dataCart);
                                                                }
                                                                rv.setAdapter(adapter);
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        })
                                                        .addOnFailureListener(PelajaranActivity.this, new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        });


                                            }
                                            // Jika tidak ada Cart
                                            else {
                                                ArrayList<PelajaranModel> dataCart = new ArrayList<>();
                                                adapter = new PelajaranAdapter(PelajaranActivity.this, dataPelajaran, dataCart);
                                                rv.setAdapter(adapter);
                                                progressBar.setVisibility(View.GONE);
                                            }

                                        }
                                    });

                        }

                        // Jika Pelajaran Tidak Ada
                        else {
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                });

    }


    private void removeKuota(String idPelajaran) {

        firestoreRoot.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = firestoreRoot.document("pelajaran/" + idPelajaran);
                DocumentSnapshot dataSnapshot = transaction.get(docRef);
                // Logic
                long kuota = dataSnapshot.getLong("kuota");
                if (kuota > 0) {
                    long sisaKuota = kuota - 1;
                    transaction.update(docRef, "kuota", sisaKuota);
                }
                // End Logic
                return null;
            }
        })
        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.GONE);
                setSnackbar("Add Success!");
            }
        })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                setSnackbar("Add Failed!");
            }
        });
    }

    private void addKuota(String idPelajaran) {

        firestoreRoot.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = firestoreRoot.document("pelajaran/" + idPelajaran);
                DocumentSnapshot dataSnapshot = transaction.get(docRef);
                // Logic
                long kuota = dataSnapshot.getLong("kuota");
                long sisaKuota = kuota + 1;
                // End Logic
                transaction.update(docRef, "kuota", sisaKuota);
                return null;
            }
        })
        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.GONE);
                setSnackbar("Delete Success!");
            }
        })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                setSnackbar("Delete Failed!");
            }
        });
    }

    @Override
    public void onItemAddToCart(PelajaranModel dataPelajaran) {
        progressBar.setVisibility(View.VISIBLE);
        CartModel dataCart = new CartModel(System.currentTimeMillis());
        firestoreRoot.document("carts/CART_" + uid + "/items/" + dataPelajaran.getId()).set(dataCart)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        removeKuota(dataPelajaran.getId());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setSnackbar("Add Failed!");
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onItemDeleteFromCart(PelajaranModel dataPelajaran) {
        progressBar.setVisibility(View.VISIBLE);
        firestoreRoot.document("carts/CART_" + uid + "/items/" + dataPelajaran.getId()).delete()
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addKuota(dataPelajaran.getId());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        setSnackbar("Delete Failed!");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.iv_back_pelajaran) {
            finish();
        }
    }
}