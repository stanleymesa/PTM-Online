package com.ptm.ppb_project.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelajaran);

        // Hooks
        ivBack = findViewById(R.id.iv_back_pelajaran);
        rv = findViewById(R.id.rv_pelajaran);

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
    }


    private void setPelajaranAdapter(String matpel) {

        String kelas = loginSession.getLoginSessionData().getKelas();

        Query query = firestoreRoot.collection("pelajaran")
                .orderBy("start_at", Query.Direction.ASCENDING)
                .whereEqualTo("matpel", matpel)
                .whereEqualTo("kelas", kelas);

        FirestoreRecyclerOptions<PelajaranModel> options = new FirestoreRecyclerOptions.Builder<PelajaranModel>()
                .setLifecycleOwner(this)
                .setQuery(query, PelajaranModel.class)
                .build();

        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        // CEK CART
        firestoreRoot.collection("carts/CART_" + uid + "/items")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            ArrayList<CartModel> dataCart = new ArrayList<>();
                            for (DocumentSnapshot ds : value) {
                                dataCart.add(ds.toObject(CartModel.class));
                            }

                            adapter = new PelajaranAdapter(options, PelajaranActivity.this, dataCart);
                            rv.setAdapter(adapter);

                        }

                        else {
                            ArrayList<CartModel> dataCart = new ArrayList<>();
                            adapter = new PelajaranAdapter(options, PelajaranActivity.this, dataCart);
                            rv.setAdapter(adapter);

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
                Toast.makeText(getBaseContext(), "Add Success!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getBaseContext(), "Delete Success!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemAddToCart(PelajaranModel dataPelajaran) {
        CartModel dataCart = new CartModel(dataPelajaran, System.currentTimeMillis());
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
                        Toast.makeText(getBaseContext(), "Add Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemDeleteFromCart(PelajaranModel dataPelajaran) {
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
                        Toast.makeText(getBaseContext(), "Delete Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_back_pelajaran) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }
}