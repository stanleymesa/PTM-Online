package com.ptm.ppb_project.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.admin.AdminDashboardActivity;
import com.ptm.ppb_project.model.UserModel;
import com.ptm.ppb_project.session.SessionManager;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {

    MaterialButton btnLogin;
    MaterialButton btnRegisterAtLogin;
    MaterialButton btnLupaPassword;
    ImageView ivCloseLogin;
    TextInputLayout tiNoHp;
    TextInputLayout tiPassword;
    String noHp;
    String password;
    CheckBox rememberMeCheckbox;
    FirebaseAuth mAuth;
    FirebaseFirestore firestoreRoot;
    SessionManager loginSession, rememberMeSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hooks
        btnLogin = findViewById(R.id.btn_login);
        ivCloseLogin = findViewById(R.id.btn_close_login);
        btnRegisterAtLogin = findViewById(R.id.btn_sudah_punya_akun);
        btnLupaPassword = findViewById(R.id.btn_lupapassword);
        tiNoHp = findViewById(R.id.ti_noHp_login);
        tiPassword = findViewById(R.id.ti_password_login);
        rememberMeCheckbox = findViewById(R.id.checkbox_rememberme);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Set Session
        loginSession = new SessionManager(this, SessionManager.LOGIN_SESSION);
        rememberMeSession = new SessionManager(this, SessionManager.REMEMBERME_SESSION);

        setEditTextLoginSession();

        btnLogin.setOnClickListener(this);
        btnRegisterAtLogin.setOnClickListener(this);
        btnLupaPassword.setOnClickListener(this);
        ivCloseLogin.setOnClickListener(this);
    }

    private void setEditTextLoginSession() {
        String noHp = loginSession.getLoginSessionData().getNoHp().replace("+62", "0");
        assert tiNoHp.getEditText() != null;
        tiNoHp.getEditText().setText(noHp);
    }

    private boolean validatePassword() {
        if (tiPassword.getEditText() != null) {
            password = tiPassword.getEditText().getText().toString().trim();
            Pattern regex = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");

            if (password.isEmpty()) {
                tiPassword.setError("Password tidak boleh kosong!");
                return false;
            }
            else {
                if (!regex.matcher(password).matches()) {
                    tiPassword.setError("Password lemah!\n(minimal 8 karakter, gunakan 1 huruf besar dan angka)");
                    return false;
                } else {
                    tiPassword.setError(null);
                    return true;
                }
            }
        }
        return false;
    }

    private void validateNoHp() {
        if (tiNoHp.getEditText() != null) {
            String noHpAwal = tiNoHp.getEditText().getText().toString();
            if (noHpAwal.isEmpty()) {
                tiNoHp.setError("Nomor Handphone tidak boleh kosong!");
                tiPassword.setError(null);
            } else {
                String firstDigit = String.valueOf(noHpAwal.charAt(0));
                if (firstDigit.equals("0")) {
                    noHp = "+62" + noHpAwal.substring(1);
                } else {
                    noHp = "+62" + noHpAwal;
                }

                // Cek apakah nomor HP ada di DB
                firestoreRoot.collection("users").whereEqualTo("noHp", noHp)
                        .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    return;
                                }

                                // Jika nomor HP ada di DB
                                if (value != null && !value.isEmpty()) {
                                    tiNoHp.setError(null);

                                    if (validatePassword()) {
                                        UserModel dataUser = new UserModel();
                                        for (DocumentSnapshot ds : value) {
                                            dataUser = ds.toObject(UserModel.class);
                                        }

                                        // Jika Password valid dgn password DB
                                        assert dataUser != null;
                                        if (dataUser.getPassword().equals(password)) {

                                            // Jika Akun sama dengan First Auth, tidak perlu verify
                                            if (isSameWithCurrentAuth()) {
                                                loginSession.createLoginSession(dataUser);
                                                if (rememberMeCheckbox.isChecked()) {
                                                    rememberMeSession.createRememberMeSession();
                                                }

                                                Intent intent;
                                                if (dataUser.getRole().equals("user")) {
                                                    intent = new Intent(getBaseContext(), DashboardActivity.class);
                                                } else {
                                                    intent = new Intent(getBaseContext(), AdminDashboardActivity.class);
                                                }
                                                Toast.makeText(getBaseContext(), "Berhasil Login!", Toast.LENGTH_SHORT).show();
                                                startActivity(intent);
                                                finish();

                                            }
                                            // Jika Akun beda dengan First Auth, perlu verify
                                            else {
                                                Intent intent = new Intent(getBaseContext(), VerifyOtpActivity.class);
                                                intent.putExtra("from", "login");
                                                intent.putExtra("dataUser", dataUser);
                                                if (rememberMeCheckbox.isChecked()) {
                                                    intent.putExtra("isRememberMe", true);
                                                } else {
                                                    intent.putExtra("isRememberMe", false);
                                                }
                                                startActivity(intent);
                                            }

                                        }
                                        // Jika Password tidak sesuai dengan DB
                                        else {
                                            tiPassword.setError("Password anda salah!");
                                        }

                                    }

                                }

                                // Jika nomor HP tidak ada di DB
                                else {
                                    tiNoHp.setError("Nomor handphone belum terdaftar!");
                                    tiPassword.setError(null);
                                }

                            }
                        });

            }
        }
    }

    private boolean isSameWithCurrentAuth() {
        if (mAuth.getCurrentUser() != null) {
            if (Objects.equals(mAuth.getCurrentUser().getPhoneNumber(), noHp)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();
        if (btnId == R.id.btn_login) {
            validateNoHp();
        }

        if (btnId == R.id.btn_sudah_punya_akun) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }

        if (btnId == R.id.btn_lupapassword) {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        }

        if (btnId == R.id.btn_close_login) {
            MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("Close Application")
                    .setCancelable(true)
                    .setMessage("Apakah anda yakin ingin keluar?")
                    .setIcon(R.drawable.ic_baseline_directions_run_24)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}