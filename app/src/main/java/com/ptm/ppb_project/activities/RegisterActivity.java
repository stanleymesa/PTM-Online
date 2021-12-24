package com.ptm.ppb_project.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.data.DataKelas;
import com.ptm.ppb_project.model.UserModel;
import com.ptm.ppb_project.session.SessionManager;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    AutoCompleteTextView dropdownKelas;
    AutoCompleteTextView dropdownNamaKelas;
    TextInputLayout tiFullname, tiKelas, tiNamaKelas, tiAbsen, tiEmail, tiNoHp, tiPassword;
    MaterialButton btnRegister;
    FirebaseFirestore firestoreRoot;
    String fullname, kelas, namaKelas, absen, email, noHp, password;
    SessionManager loginSession;
    String role = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Hooks
        dropdownKelas = findViewById(R.id.dropdown_kelas_register);
        dropdownNamaKelas = findViewById(R.id.dropdown_namakelas_register);
        btnRegister = findViewById(R.id.btn_register);
        tiFullname = findViewById(R.id.ti_fullname_register);
        tiKelas = findViewById(R.id.ti_kelas_register);
        tiNamaKelas = findViewById(R.id.ti_namakelas_register);
        tiAbsen = findViewById(R.id.ti_noabsen_register);
        tiEmail = findViewById(R.id.ti_email_register);
        tiNoHp = findViewById(R.id.ti_noHp_register);
        tiPassword = findViewById(R.id.ti_password_register);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();

        // Set Session
        loginSession = new SessionManager(this, SessionManager.LOGIN_SESSION);

        setDropdownKelas();

        btnRegister.setOnClickListener(this);

    }

    private void validateFullname() {
        if (tiFullname.getEditText() != null) {

            fullname = tiFullname.getEditText().getText().toString().trim();
            if (fullname.isEmpty()) {
                tiFullname.setError("Fullname tidak boleh kosong!");
            }
            else {
                tiFullname.setError(null);
            }
        }

    }

    private void validateKelas() {
        kelas = dropdownKelas.getText().toString().trim();
        if (kelas.isEmpty()) {
            tiKelas.setError("Kelas harus dipilih!");
        } else {
            tiKelas.setError(null);
        }
    }

    private void validateNamaKelas() {
        namaKelas = dropdownNamaKelas.getText().toString().trim();
        if (namaKelas.isEmpty()) {
            tiNamaKelas.setError("Nama Kelas harus dipilih!");
        } else {
            tiNamaKelas.setError(null);
        }
    }

    private void validateAbsen() {
        if (tiAbsen.getEditText() != null) {
            absen = tiAbsen.getEditText().getText().toString().trim();
            if (absen.isEmpty()) {
                tiAbsen.setError("No.Absen tidak boleh kosong!");
            }
            else {
                tiAbsen.setError(null);
            }
        }
    }

    private void validateEmail(boolean goIntent) {
        if (tiEmail.getEditText() != null) {
            email = tiEmail.getEditText().getText().toString().trim();
            Pattern regex = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
            if (email.isEmpty()) {
                tiEmail.setError("Email tidak boleh kosong!");
            }
            else {
                if (!regex.matcher(email).matches()) {
                    tiEmail.setError("Email tidak sesuai (contoh@email.com)");
                } else {

                    // Cek apakah ada email di DB
                    firestoreRoot.collection("users").whereEqualTo("email", email)
                            .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        return;
                                    }

                                    // Jika email ada di DB
                                    if (value != null && !value.isEmpty()) {
                                        tiEmail.setError("Email telah dipakai");
                                    }

                                    // Jika email tidak ada di DB
                                    else {
                                        tiEmail.setError(null);

                                        if (!goIntent) {
                                            return;
                                        }
                                        // Intent to Verify OTP
                                        Intent intent = new Intent(getBaseContext(), VerifyOtpActivity.class);
                                        intent.putExtra("from", "register");
                                        intent.putExtra("dataUser", getDataUser());
                                        startActivity(intent);

                                    }
                                }
                            });
                }

            }
        }
    }

    private void validateNoHp() {
        if (tiNoHp.getEditText() != null) {
            String noHpAwal = tiNoHp.getEditText().getText().toString().trim();
            if (noHpAwal.isEmpty()) {
                tiNoHp.setError("Nomor Handphone tidak boleh kosong!");
                allValidation();
            }
            else {
                String firstDigit = String.valueOf(noHpAwal.charAt(0));
                if (firstDigit.equals("0")) {
                    noHp = "+62" + noHpAwal.substring(1);
                } else {
                    noHp = "+62" + noHpAwal;
                }

                // Cek apakah nomor HP sudah dipakai di DB
                firestoreRoot.collection("users").whereEqualTo("noHp", noHp)
                        .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    return;
                                }

                                // Jika nomor HP ada di DB
                                if (value != null && !value.isEmpty()) {
                                    tiNoHp.setError("Nomor Handphone telah dipakai");
                                    allValidation();
                                }

                                // Jika nomor HP tidak ada di DB
                                else {
                                    tiNoHp.setError(null);
                                    if (allValidation()) {
                                        validateEmail(true);
                                    }

                                }
                            }
                        });

            }
        }
    }

    private void validatePassword() {
        if (tiPassword.getEditText() != null) {
            password = tiPassword.getEditText().getText().toString().trim();
            Pattern regex = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");

            if (password.isEmpty()) {
                tiPassword.setError("Password tidak boleh kosong!");
            }
            else {
                tiPassword.setError(null);
                if (!regex.matcher(password).matches()) {
                    tiPassword.setError("Password lemah!\n(minimal 8 karakter, gunakan 1 huruf besar dan angka)");
                } else {
                    tiPassword.setError(null);
                }
            }
        }
    }

    private boolean allValidation() {
        validateFullname();
        validateKelas();
        validateNamaKelas();
        validateAbsen();
        validateEmail(false);
        validatePassword();

        if (
            tiFullname.getError() != null ||
            tiKelas.getError() != null ||
            tiNamaKelas.getError() != null ||
            tiAbsen.getError() != null ||
            tiEmail.getError() != null ||
            tiPassword.getError() != null
        ) {
            return false;
        }
        return true;
    }

    private void setDropdownKelas() {
        ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(
                this, R.layout.item_dropdown, new DataKelas().getKelas()
        );
        ArrayAdapter<String> namaKelasAdapter = new ArrayAdapter<>(
                this, R.layout.item_dropdown, new DataKelas().getNamaKelas()
        );

        dropdownKelas.setAdapter(kelasAdapter);
        dropdownNamaKelas.setAdapter(namaKelasAdapter);
    }


    private UserModel getDataUser() {
        UserModel dataUser = new UserModel(fullname, kelas, namaKelas, absen, email, noHp, password, role, String.valueOf(System.currentTimeMillis()));
        return dataUser;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_register) {
            validateNoHp();
        }
    }
}