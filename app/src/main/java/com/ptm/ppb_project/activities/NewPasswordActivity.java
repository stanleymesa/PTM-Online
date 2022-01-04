package com.ptm.ppb_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.session.SessionManager;

import java.util.regex.Pattern;

public class NewPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout tiNewPassword, tiConfirmPassword;
    MaterialButton btnUpdate;
    FirebaseFirestore firestoreRoot;
    FirebaseAuth mAuth;
    String newPassword, confirmPassword;
    SessionManager updatePasswordSession;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        // Hooks
        tiNewPassword = findViewById(R.id.ti_newpassword);
        tiConfirmPassword = findViewById(R.id.ti_confirmpassword);
        btnUpdate = findViewById(R.id.btn_update_password);
        ivBack = findViewById(R.id.iv_back_updatepassword);

        // Set Session
        updatePasswordSession = new SessionManager(this, SessionManager.UPDATE_PASSWORD_SESSION);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // On Click
        btnUpdate.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }


    private void validateNewPassword() {
        assert tiNewPassword.getEditText() != null;
        assert tiConfirmPassword.getEditText() != null;
        newPassword = tiNewPassword.getEditText().getText().toString().trim();
        confirmPassword = tiConfirmPassword.getEditText().getText().toString().trim();

        Pattern regex = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");

        if (newPassword.isEmpty()) {
            tiNewPassword.setError("Password tidak boleh kosong!");
        }
        else {
            if (!regex.matcher(newPassword).matches()) {
                tiNewPassword.setError("Password lemah!\n(minimal 8 karakter, gunakan 1 huruf besar dan angka)");
            } else {

                // Cek apakah new password == confirm password
                if (newPassword.equals(confirmPassword)) {
                    tiNewPassword.setError(null);
                    tiConfirmPassword.setError(null);

                    // Update password di DB
                    assert mAuth.getCurrentUser() != null;
                    firestoreRoot.document("users/" + mAuth.getCurrentUser().getUid()).update("password", newPassword)
                            .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    updatePasswordSession.createUpdatePasswordSession(true, true);
                                    finish();
                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    updatePasswordSession.createUpdatePasswordSession(true, false);
                                    finish();
                                }
                            });

                }
                else {
                    tiConfirmPassword.setError("Password anda berbeda!");
                }
            }
        }
    }



    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_update_password) {
            validateNewPassword();
        }

        if (btnId == R.id.iv_back_updatepassword) {
            MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("Back to Login")
                    .setCancelable(true)
                    .setMessage("Apakah anda yakin ingin kembali ke login?")
                    .setIcon(R.drawable.ic_baseline_directions_run_24)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alertDialog.show();
        }

    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Back to Login")
                .setCancelable(true)
                .setMessage("Apakah anda yakin ingin kembali ke login?")
                .setIcon(R.drawable.ic_baseline_directions_run_24)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.show();
    }
}