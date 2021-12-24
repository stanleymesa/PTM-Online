package com.ptm.ppb_project.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.model.PelajaranModel;

import java.util.ArrayList;
import java.util.UUID;

public class AddLessonsActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout tiMatpel;
    TextInputLayout tiMateri;
    TextInputLayout tiKelas;
    TextInputLayout tiHari;
    TextInputLayout tiStartAt;
    TextInputLayout tiFinishAt;
    TextInputLayout tiKuota;
    MaterialButton btnAddLessons;
    FirebaseFirestore firestoreRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lessons);

        // Hooks
        tiMatpel = findViewById(R.id.ti_matpel);
        tiMateri = findViewById(R.id.ti_materi);
        tiKelas = findViewById(R.id.ti_kelas);
        tiHari = findViewById(R.id.ti_hari);
        tiStartAt = findViewById(R.id.ti_start_at);
        tiFinishAt = findViewById(R.id.ti_finish_at);
        tiKuota = findViewById(R.id.ti_kuota);
        btnAddLessons = findViewById(R.id.btn_addlessons);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();

        // On Click
        btnAddLessons.setOnClickListener(this);

    }

    private void addLessonToDB() {
        String id = UUID.randomUUID().toString();
        PelajaranModel pelajaranModel = new PelajaranModel(
                id,
                tiMatpel.getEditText().getText().toString(),
                tiMateri.getEditText().getText().toString(),
                tiKelas.getEditText().getText().toString(),
                tiHari.getEditText().getText().toString(),
                Long.parseLong(tiStartAt.getEditText().getText().toString()),
                Long.parseLong(tiFinishAt.getEditText().getText().toString()),
                Long.parseLong(tiKuota.getEditText().getText().toString()),
                generateKeywords(tiMateri.getEditText().getText().toString())
        );

        firestoreRoot.collection("pelajaran").document(id).set(pelajaranModel)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addLessonsToStats();
                        Toast.makeText(getBaseContext(), "Berhasil Input Lessons", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addLessonsToStats() {
        firestoreRoot.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = firestoreRoot.document("stats/qty");
                DocumentSnapshot dataSnapshot = transaction.get(docRef);
                // Logic
                long newStat = dataSnapshot.getLong("pelajaran") + 1;
                transaction.update(docRef, "pelajaran", newStat);
                return null;
            }
        });
    }

    private ArrayList<String> generateKeywords(String materi) {
        String text = materi.toLowerCase().trim();
        ArrayList<String> key = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            for (int j = i; j < text.length(); j++) {
                key.add(text.substring(i, j+1));
            }
        }
        return key;
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_addlessons) {
            addLessonToDB();
        }
    }
}