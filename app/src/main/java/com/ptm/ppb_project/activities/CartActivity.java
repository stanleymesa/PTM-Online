package com.ptm.ppb_project.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.ptm.ppb_project.adapter.CartAdapter;
import com.ptm.ppb_project.model.CartModel;
import com.ptm.ppb_project.model.PelajaranModel;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnItemClickCallback, View.OnClickListener {

    RecyclerView rvCart;
    FirebaseAuth mAuth;
    FirebaseFirestore firestoreRoot;
    CartAdapter adapter;
    ImageView ivBack;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Hooks
        rvCart = findViewById(R.id.rv_cart);
        ivBack = findViewById(R.id.btn_back_cart);
        progressBar = findViewById(R.id.progressbar_cart);

        // Set Firebase
        mAuth = FirebaseAuth.getInstance();
        firestoreRoot = FirebaseFirestore.getInstance();

        // On Click
        ivBack.setOnClickListener(this);

        setCartAdapter();
    }

    private void setCartAdapter() {
        assert mAuth.getCurrentUser() != null;

        rvCart.setLayoutManager(new LinearLayoutManager(this));

        firestoreRoot.collection("carts/CART_" + mAuth.getCurrentUser().getUid() + "/items")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        // Jika ada cart
                        if (value != null && !value.isEmpty()) {
                            ArrayList<String> cartId = new ArrayList<>();
                            ArrayList<Long> cartCreatedAt = new ArrayList<>();
                            for (DocumentSnapshot ds : value) {
                                cartId.add(ds.getId());
                                cartCreatedAt.add(ds.getLong("created_at"));
                            }

                            Query query = firestoreRoot.collection("pelajaran").whereIn(FieldPath.documentId(), cartId);
                            FirestoreRecyclerOptions<PelajaranModel> options = new FirestoreRecyclerOptions.Builder<PelajaranModel>()
                                    .setLifecycleOwner(CartActivity.this)
                                    .setQuery(query, PelajaranModel.class)
                                    .build();
                            adapter = new CartAdapter(options, CartActivity.this, cartCreatedAt);
                            rvCart.setAdapter(adapter);
                        }

                        // Jika tidak ada cart
                        else {
                            rvCart.setVisibility(View.GONE);
                        }

                    }
                });

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

    private void addKuota(String idPelajaran) {
        firestoreRoot.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = firestoreRoot.document("pelajaran/" + idPelajaran);
                DocumentSnapshot snapshot = transaction.get(docRef);
                // Logic
                long kuota = snapshot.getLong("kuota");
                long sisaKuota = kuota + 1;
                // End Logic
                transaction.update(docRef, "kuota", sisaKuota);
                return null;
            }
        }).addOnSuccessListener(this, new OnSuccessListener<Void>() {
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
    public void onItemDeleteFromCart(PelajaranModel dataCart) {
        progressBar.setVisibility(View.VISIBLE);
        assert mAuth.getCurrentUser() != null;
        firestoreRoot.document("carts/CART_" + mAuth.getCurrentUser().getUid() + "/items/" + dataCart.getId()).delete()
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addKuota(dataCart.getId());
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

        if (btnId == R.id.btn_back_cart) {
            finish();
        }
    }

}