package com.ptm.ppb_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptm.ppb_project.R;

import java.util.regex.Pattern;

public class NewPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout tiNewPassword, tiConfirmPassword;
    MaterialButton btnUpdate;
    FirebaseFirestore firestoreRoot;
    FirebaseAuth mAuth;
    String newPassword, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        // Hooks
        tiNewPassword = findViewById(R.id.ti_newpassword);
        tiConfirmPassword = findViewById(R.id.ti_confirmpassword);
        btnUpdate = findViewById(R.id.btn_update_password);

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // On Click
        btnUpdate.setOnClickListener(this);
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
                                    Toast.makeText(getBaseContext(), "Update Password Berhasil!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getBaseContext(), "Update Password Gagal!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                    startActivity(intent);
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
    }
}