package com.ptm.ppb_project.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.data.DataKelas;
import com.ptm.ppb_project.model.PelajaranModel;
import com.ptm.ppb_project.timer.TimerPickerFragment;

import java.util.ArrayList;
import java.util.UUID;

public class AddLessonsActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener{

    TextInputLayout tiMatpel;
    TextInputLayout tiMateri;
    TextInputLayout tiKelas;
    TextInputLayout tiHari;
    TextInputLayout tiStartAt;
    TextInputLayout tiFinishAt;
    TextInputLayout tiKuota;
    TextInputEditText etStartAt;
    TextInputEditText etFinishAt;
    AutoCompleteTextView dropdownKelas;
    AutoCompleteTextView dropdownHari;
    AutoCompleteTextView dropdownMatpel;
    MaterialButton btnAddLessons;
    MaterialButton btnPickStart;
    MaterialButton btnPickFinish;
    FirebaseFirestore firestoreRoot;
    String timePickerName = "";
    String matpel, materi, kelas, hari, startAt, finishAt, kuota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lessons);

        // Hooks
        tiMatpel = findViewById(R.id.ti_matpel_addlessons);
        tiMateri = findViewById(R.id.ti_materi_addlessons);
        tiKelas = findViewById(R.id.ti_kelas_addlessons);
        tiHari = findViewById(R.id.ti_hari_addlessons);
        tiStartAt = findViewById(R.id.ti_start_at_addlessons);
        tiFinishAt = findViewById(R.id.ti_finish_at_addlessons);
        tiKuota = findViewById(R.id.ti_kuota);
        dropdownKelas = findViewById(R.id.dropdown_kelas_addlessons);
        dropdownHari = findViewById(R.id.dropdown_hari_addlessons);
        dropdownMatpel = findViewById(R.id.dropdown_matpel_addlessons);
        btnAddLessons = findViewById(R.id.btn_addlessons);
        etStartAt = findViewById(R.id.et_start_at_addlessons);
        etFinishAt = findViewById(R.id.et_finish_at_addlessons);
        btnPickStart = findViewById(R.id.btn_picktime_start_at_addlessons);
        btnPickFinish = findViewById(R.id.btn_picktime_finish_at_addlessons);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();

        // On Click
        btnAddLessons.setOnClickListener(this);
        btnPickStart.setOnClickListener(this);
        btnPickFinish.setOnClickListener(this);

        setDropdown();


    }

    private void addLessonToDB() {

        if (!allValidation()) {
            return;
        }

        String id = UUID.randomUUID().toString();
        PelajaranModel pelajaranModel = new PelajaranModel(
                id,
                matpel,
                materi,
                kelas,
                hari,
                Long.parseLong(cleanTimer(startAt)),
                Long.parseLong(cleanTimer(finishAt)),
                Long.parseLong(kuota),
                generateKeywords(materi)
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

    private String cleanTimer(String time) {
        return time.replace(":", "").replaceAll("\\s", "");
    }

    private void validateMatpel() {
        assert tiMatpel.getEditText() != null;
        if (tiMatpel.getEditText().getText().toString().trim().isEmpty()) {
            tiMatpel.setError("Mata Pelajaran tidak boleh kosong");
        }
        else {
            tiMatpel.setError(null);
            matpel = tiMatpel.getEditText().getText().toString().trim();
        }
    }

    private void validateMateri() {
        assert tiMateri.getEditText() != null;
        if (tiMateri.getEditText().getText().toString().trim().isEmpty()) {
            tiMateri.setError("Materi tidak boleh kosong");
        }
        else {
            tiMateri.setError(null);
            materi = tiMateri.getEditText().getText().toString().trim();
        }
    }

    private void validateKelas() {
        assert tiKelas.getEditText() != null;
        if (tiKelas.getEditText().getText().toString().trim().isEmpty()) {
            tiKelas.setError("Kelas tidak boleh kosong");
        }
        else {
            tiKelas.setError(null);
            kelas = tiKelas.getEditText().getText().toString().trim();
        }
    }

    private void validateHari() {
        assert tiHari.getEditText() != null;
        if (tiHari.getEditText().getText().toString().trim().isEmpty()) {
            tiHari.setError("Hari tidak boleh kosong");
        }
        else {
            tiHari.setError(null);
            hari = tiHari.getEditText().getText().toString().trim();
        }
    }

    private void validateStartAt() {
        assert tiStartAt.getEditText() != null;
        if (tiStartAt.getEditText().getText().toString().trim().isEmpty()) {
            tiStartAt.setError("Start At tidak boleh kosong");
        }
        else {
            tiStartAt.setError(null);
            startAt = tiStartAt.getEditText().getText().toString().trim();
        }
    }

    private void validateFinishAt() {
        assert tiFinishAt.getEditText() != null;
        if (tiFinishAt.getEditText().getText().toString().trim().isEmpty()) {
            tiFinishAt.setError("Finish At tidak boleh kosong");
        }
        else {
            tiFinishAt.setError(null);
            finishAt = tiFinishAt.getEditText().getText().toString().trim();
        }
    }

    private void validateKuota() {
        assert tiKuota.getEditText() != null;
        if (tiKuota.getEditText().getText().toString().trim().isEmpty()) {
            tiKuota.setError("Kuota tidak boleh kosong");
        }
        else {
            tiKuota.setError(null);
            kuota = tiKuota.getEditText().getText().toString().trim();
        }
    }

    private boolean allValidation() {
        validateMatpel();
        validateMateri();
        validateKelas();
        validateHari();
        validateStartAt();
        validateFinishAt();
        validateKuota();

        return tiMatpel.getError() == null
                && tiMateri.getError() == null
                && tiKelas.getError() == null
                && tiHari.getError() == null
                && tiStartAt.getError() == null
                && tiFinishAt.getError() == null
                && tiKuota.getError() == null;
    }


    private ArrayList<String> generateKeywords(String materi) {
        String text = materi.toLowerCase().trim();
        ArrayList<String> key = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            for (int j = i; j < text.length(); j++) {
                key.add(text.substring(i, j + 1));
            }
        }
        return key;
    }

    private void setDropdown() {
        DataKelas dataKelas = new DataKelas();
        ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, dataKelas.getKelas());
        ArrayAdapter<String> hariAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, dataKelas.getHari());
        ArrayAdapter<String> matpelAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, dataKelas.getMatpel());

        dropdownKelas.setAdapter(kelasAdapter);
        dropdownHari.setAdapter(hariAdapter);
        dropdownMatpel.setAdapter(matpelAdapter);
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_addlessons) {
            addLessonToDB();
        }

        if (btnId == R.id.btn_picktime_start_at_addlessons) {
            timePickerName = "start_at";
            DialogFragment timePicker = new TimerPickerFragment();
            timePicker.show(getSupportFragmentManager(), "time_picker");
        }

        if (btnId == R.id.btn_picktime_finish_at_addlessons) {
            timePickerName = "finish_at";
            DialogFragment timePicker = new TimerPickerFragment();
            timePicker.show(getSupportFragmentManager(), "time_picker");
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
        if (timePickerName.equals("start_at")) {
            String jam, menit = "";
            if (String.valueOf(hour).length() == 1) {
                jam = "0" + hour;
            } else {
                jam = String.valueOf(hour);
            }

            if (String.valueOf(minutes).length() == 1){
                menit = "0" + minutes;
            } else {
                menit = String.valueOf(minutes);
            }

            etStartAt.setText(jam + " : " + menit);
        }
        else if (timePickerName.equals("finish_at")) {
            String jam, menit = "";
            if (String.valueOf(hour).length() == 1) {
                jam = "0" + hour;
            } else {
                jam = String.valueOf(hour);
            }

            if (String.valueOf(minutes).length() == 1){
                menit = "0" + minutes;
            } else {
                menit = String.valueOf(minutes);
            }
            etFinishAt.setText(jam + " : " + menit);
        }

    }

}