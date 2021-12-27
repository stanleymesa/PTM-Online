package com.ptm.ppb_project.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
        Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
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
}