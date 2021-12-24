package com.ptm.ppb_project.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptm.ppb_project.R;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout tiNoHp;
    MaterialButton btnNext;
    FirebaseFirestore firestoreRoot;
    String noHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Hooks
        tiNoHp = findViewById(R.id.ti_noHp_forgotpassword);
        btnNext = findViewById(R.id.btn_next_forgotpassword);


        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();

        // On Click
        btnNext.setOnClickListener(this);

    }

    private void validateNoHp() {
        if (tiNoHp.getEditText() != null) {
            String noHpAwal = tiNoHp.getEditText().getText().toString();
            if (noHpAwal.isEmpty()) {
                tiNoHp.setError("Nomor Handphone tidak boleh kosong!");
            } else {
                String firstDigit = String.valueOf(noHpAwal.charAt(0));
                if (firstDigit.equals("0")) {
                    noHp = "+62" + noHpAwal.substring(1);
                } else {
                    noHp = "+62" + noHpAwal;
                }

                // Cek apakah Nomor HP ada di DB
                firestoreRoot.collection("users").whereEqualTo("noHp", noHp)
                        .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    return;
                                }

                                // Jika ada nomor HP di DB
                                if (value != null && !value.isEmpty()) {
                                    tiNoHp.setError(null);
                                    Intent intent = new Intent(getBaseContext(), VerifyOtpActivity.class);
                                    intent.putExtra("from", "forgotPassword");
                                    intent.putExtra("noHp", noHp);
                                    startActivity(intent);
                                    finish();
                                }

                                // Jika tidak ada nomor HP di DB
                                else {
                                    tiNoHp.setError("Nomor Handphone belum terdaftar!");
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_next_forgotpassword) {
            validateNoHp();
        }
    }
}