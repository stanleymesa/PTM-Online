package com.ptm.ppb_project.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.model.PelajaranModel;

public class SearchLessonsActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout tiSearch;
    FloatingActionButton fabAddLessons;
    FirebaseFirestore firestoreRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lessons);

        // Hooks
        tiSearch = findViewById(R.id.ti_searchLessons);
        fabAddLessons = findViewById(R.id.fab_add_lessons);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();

        // On Click
        fabAddLessons.setOnClickListener(this);

        assert tiSearch.getEditText() != null;
        tiSearch.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(getApplicationContext(), tiSearch.getEditText().getText(), Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });


    }

    private void setRecycler() {
        Query query = firestoreRoot.collection("pelajaran")
                .orderBy("kelas", Query.Direction.ASCENDING)
                .orderBy("start_at", Query.Direction.ASCENDING)
                .limit(3);

        FirestoreRecyclerOptions<PelajaranModel> options = new FirestoreRecyclerOptions.Builder<PelajaranModel>()
                .setLifecycleOwner(this)
                .setQuery(query, PelajaranModel.class)
                .build();
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.fab_add_lessons) {
            startActivity(new Intent(this, AddLessonsActivity.class));
        }
    }
}