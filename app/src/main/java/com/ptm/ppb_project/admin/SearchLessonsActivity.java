package com.ptm.ppb_project.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.adapter.SearchLessonsAdapter;
import com.ptm.ppb_project.model.PelajaranModel;

import java.util.ArrayList;

public class SearchLessonsActivity extends AppCompatActivity implements View.OnClickListener, SearchLessonsAdapter.OnItemClickCallback {

    TextInputLayout tiSearch;
    RecyclerView rvSearch;
    FloatingActionButton fabAddLessons;
    FirebaseFirestore firestoreRoot;
    SearchLessonsAdapter adapter;
    DocumentSnapshot lastVisible;
    String hint = "";
    ArrayList<PelajaranModel> listPelajaran = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lessons);

        // Hooks
        tiSearch = findViewById(R.id.ti_searchLessons);
        fabAddLessons = findViewById(R.id.fab_add_lessons);
        rvSearch = findViewById(R.id.rv_search_lessons);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();

        // On Click
        fabAddLessons.setOnClickListener(this);

        setSearch();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initiateRecycler(hint);
    }

    private void initiateRecycler(String hint) {
        listPelajaran.clear();
        rvSearch.setLayoutManager(new LinearLayoutManager(this));

        if (hint.isEmpty()) {
            firestoreRoot.collection("pelajaran")
                    .orderBy("kelas")
                    .orderBy("matpel")
                    .orderBy("start_at")
                    .limit(5)
                    .get()
                    .addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            if (queryDocumentSnapshots.isEmpty()) {
                                return;
                            }

                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                listPelajaran.add(ds.toObject(PelajaranModel.class));
                            }
                            adapter = new SearchLessonsAdapter(listPelajaran, SearchLessonsActivity.this);
                            rvSearch.setAdapter(adapter);
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    });
        }
        else {
            firestoreRoot.collection("pelajaran")
                    .whereArrayContains("slug", hint)
                    .orderBy("kelas")
                    .orderBy("matpel")
                    .orderBy("start_at")
                    .limit(5)
                    .get()
                    .addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            if (queryDocumentSnapshots.isEmpty()) {
                                return;
                            }

                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                listPelajaran.add(ds.toObject(PelajaranModel.class));
                            }
                            adapter = new SearchLessonsAdapter(listPelajaran, SearchLessonsActivity.this);
                            rvSearch.setAdapter(adapter);
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    });
        }

    }

    private void showMoreRecycler(String hint) {

        if (hint.isEmpty()) {
            firestoreRoot.collection("pelajaran")
                    .orderBy("kelas")
                    .orderBy("matpel")
                    .orderBy("start_at")
                    .startAfter(lastVisible)
                    .limit(5)
                    .get()
                    .addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            if (queryDocumentSnapshots.isEmpty()) {
                                return;
                            }

                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                listPelajaran.add(ds.toObject(PelajaranModel.class));
                            }
                            adapter.notifyDataSetChanged();
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    });
        }
        else {
            firestoreRoot.collection("pelajaran")
                    .whereArrayContains("slug", hint)
                    .orderBy("kelas")
                    .orderBy("matpel")
                    .orderBy("start_at")
                    .startAfter(lastVisible)
                    .limit(5)
                    .get()
                    .addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            if (queryDocumentSnapshots.isEmpty()) {
                                return;
                            }

                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                listPelajaran.add(ds.toObject(PelajaranModel.class));
                            }
                            adapter.notifyDataSetChanged();
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    });
        }

    }

    private void setSearch() {
        assert tiSearch.getEditText() != null;
        tiSearch.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hint = tiSearch.getEditText().getText().toString().toLowerCase();
                    initiateRecycler(hint);
                }

                return false;
            }
        });
    }

    private void removeLessonStats() {
        firestoreRoot.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = firestoreRoot.document("stats/qty");
                DocumentSnapshot dataSnapshot = transaction.get(docRef);
                // Logic
                long newStat = dataSnapshot.getLong("pelajaran") - 1;
                transaction.update(docRef, "pelajaran", newStat);
                return null;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.fab_add_lessons) {
            startActivity(new Intent(this, AddLessonsActivity.class));
        }
    }

    @Override
    public void onShowMoreClick(TextView tvShowMore) {
        tvShowMore.setVisibility(View.GONE);
        showMoreRecycler(hint);
    }

    @Override
    public void onEditLessons(PelajaranModel pelajaranModel) {
        Intent intent = new Intent(this, EditLessonsActivity.class);
        intent.putExtra("data", pelajaranModel);
        startActivity(intent);
    }

    @Override
    public void onDeleteLessons(PelajaranModel pelajaranModel) {
        new MaterialAlertDialogBuilder(this)
                .setMessage("Apakah anda yakin ingin menghapus pelajaran " + pelajaranModel.getMateri() + " ?")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firestoreRoot.document("pelajaran/" + pelajaranModel.getId()).delete()
                                .addOnSuccessListener(SearchLessonsActivity.this, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        removeLessonStats();
                                        Toast.makeText(SearchLessonsActivity.this, "Berhasil delete lesson!", Toast.LENGTH_SHORT).show();
                                        initiateRecycler(hint);
                                    }
                                })
                                .addOnFailureListener(SearchLessonsActivity.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SearchLessonsActivity.this, "Gagal delete lesson!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .show();
    }
}