package com.ptm.ppb_project.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.data.DataKelas;
import com.ptm.ppb_project.model.PelajaranModel;
import com.ptm.ppb_project.session.SessionManager;
import com.ptm.ppb_project.timer.TimerPickerFragment;

import java.util.ArrayList;
import java.util.UUID;

public class EditLessonsActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    TextInputLayout tiMatpel, tiMateri, tiKelas, tiHari, tiStartAt, tiFinishAt, tiKuota;
    TextInputEditText etStartAt, etFinishAt;
    AutoCompleteTextView dropdownKelas, dropdownHari, dropdownMatpel;
    MaterialButton btnEditlessons, btnPickStart, btnPickFinish;
    FirebaseFirestore firestoreRoot;
    String timePickerName = "";
    String matpel, materi, kelas, hari, startAt, finishAt, kuota;
    PelajaranModel dataFromIntent;
    SessionManager editLessonsSession;
    ImageView ivBack;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lessons);

        // Hooks
        tiMatpel = findViewById(R.id.ti_matpel_editlessons);
        tiMateri = findViewById(R.id.ti_materi_editlessons);
        tiKelas = findViewById(R.id.ti_kelas_editlessons);
        tiHari = findViewById(R.id.ti_hari_editlessons);
        tiStartAt = findViewById(R.id.ti_start_at_editlessons);
        tiFinishAt = findViewById(R.id.ti_finish_at_editlessons);
        tiKuota = findViewById(R.id.ti_kuota_editlessons);
        dropdownKelas = findViewById(R.id.dropdown_kelas_editlessons);
        dropdownHari = findViewById(R.id.dropdown_hari_editlessons);
        dropdownMatpel = findViewById(R.id.dropdown_matpel_editlessons);
        btnEditlessons = findViewById(R.id.btn_editlessons);
        etStartAt = findViewById(R.id.et_start_at_editlessons);
        etFinishAt = findViewById(R.id.et_finish_at_editlessons);
        btnPickStart = findViewById(R.id.btn_picktime_start_at_editlessons);
        btnPickFinish = findViewById(R.id.btn_picktime_finish_at_editlessons);
        ivBack = findViewById(R.id.iv_back_editlessons);
        progressBar = findViewById(R.id.pb_editlessons);

        // Set Session
        editLessonsSession = new SessionManager(this, SessionManager.EDIT_LESSONS_SESSION);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();

        // On Click
        btnEditlessons.setOnClickListener(this);
        btnPickStart.setOnClickListener(this);
        btnPickFinish.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        setDataFromIntent();
    }


    private void setDataFromIntent() {
        // Intent
        dataFromIntent = getIntent().getParcelableExtra("data");

        // Assert
        assert tiMatpel.getEditText() != null;
        assert tiMateri.getEditText() != null;
        assert tiKelas.getEditText() != null;
        assert tiHari.getEditText() != null;
        assert tiStartAt.getEditText() != null;
        assert tiFinishAt.getEditText() != null;
        assert tiKuota.getEditText() != null;

        tiMatpel.getEditText().setText(dataFromIntent.getMatpel());
        tiMateri.getEditText().setText(dataFromIntent.getMateri());
        tiKelas.getEditText().setText(dataFromIntent.getKelas());
        tiHari.getEditText().setText(dataFromIntent.getHari());
        tiStartAt.getEditText().setText(setTimerFromIntent(dataFromIntent.getStart_at()));
        tiFinishAt.getEditText().setText(setTimerFromIntent(dataFromIntent.getFinish_at()));
        tiKuota.getEditText().setText(String.valueOf(dataFromIntent.getKuota()));

        setDropdown();

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

    private void editLessonToDB() {

        if (!allValidation()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String id = dataFromIntent.getId();

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
                        progressBar.setVisibility(View.GONE);
                        editLessonsSession.createEditLessonsSession(true);
                        finish();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        setSnackbar("Edit Lessons Failed!");
                    }
                });
    }

    private String cleanTimer(String time) {
        return time.replace(":", "").replaceAll("\\s", "");
    }

    private String setTimerFromIntent(long time) {
        String waktuString = String.valueOf(time);

        // Menambahkan : waktuString
        StringBuilder after = new StringBuilder(waktuString);
        if (waktuString.length() == 3) {
            after.insert(1, ":");
            after.insert(1, " ");
            after.insert(3, " ");
        } else {
            after.insert(2, ":");
            after.insert(2, " ");
            after.insert(4, " ");
        }
        return after.toString();
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

    private void setDropdown() {
        DataKelas dataKelas = new DataKelas();
        ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, dataKelas.getKelas());
        ArrayAdapter<String> hariAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, dataKelas.getHari());
        ArrayAdapter<String> matpelAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, dataKelas.getMatpel());

        dropdownKelas.setAdapter(kelasAdapter);
        dropdownHari.setAdapter(hariAdapter);
        dropdownMatpel.setAdapter(matpelAdapter);
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


    @Override
    public void onClick(View view) {
        int btnId = view.getId();

        if (btnId == R.id.btn_editlessons) {
            editLessonToDB();
        }

        if (btnId == R.id.btn_picktime_start_at_editlessons) {
            timePickerName = "start_at";
            DialogFragment timePicker = new TimerPickerFragment();
            timePicker.show(getSupportFragmentManager(), "time_picker");
        }

        if (btnId == R.id.btn_picktime_finish_at_editlessons) {
            timePickerName = "finish_at";
            DialogFragment timePicker = new TimerPickerFragment();
            timePicker.show(getSupportFragmentManager(), "time_picker");
        }

        if (btnId == R.id.iv_back_editlessons) {
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
        if (timePickerName.equals("start_at")) {
            String menit = "";

            if (String.valueOf(minutes).length() == 1){
                menit = "0" + minutes;
            } else {
                menit = String.valueOf(minutes);
            }

            etStartAt.setText(hour + " : " + menit);
        }
        else if (timePickerName.equals("finish_at")) {
            String menit = "";

            if (String.valueOf(minutes).length() == 1){
                menit = "0" + minutes;
            } else {
                menit = String.valueOf(minutes);
            }
            etFinishAt.setText(hour + " : " + menit);
        }
    }
}