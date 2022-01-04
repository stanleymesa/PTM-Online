package com.ptm.ppb_project.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.admin.AdminDashboardActivity;
import com.ptm.ppb_project.model.UserModel;
import com.ptm.ppb_project.session.SessionManager;

import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvNoHp;
    ImageView ivClose;
    ProgressBar progressBar;
    PinView pinview;
    MaterialButton btnVerify;
    FirebaseAuth mAuth;
    String noHp, fromWhere, codeBySystem;
    UserModel dataUser;
    FirebaseFirestore firestoreRoot;
    SessionManager loginSession, rememberMeSession;
    boolean isRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        // Hooks
        tvNoHp = findViewById(R.id.tv_noHp_verify);
        progressBar = findViewById(R.id.pb_verify);
        ivClose = findViewById(R.id.iv_close_verify);
        pinview = findViewById(R.id.pinview_verify);
        btnVerify = findViewById(R.id.btn_verify);

        // Set Firebase
        mAuth = FirebaseAuth.getInstance();
        firestoreRoot = FirebaseFirestore.getInstance();

        // Set Session
        loginSession = new SessionManager(this, SessionManager.LOGIN_SESSION);
        rememberMeSession = new SessionManager(this, SessionManager.REMEMBERME_SESSION);

        // Get Intent
        fromWhere = getIntent().getStringExtra("from");
        // Jika dari Register
        if (fromWhere.equals("register")) {
            dataUser = getIntent().getParcelableExtra("dataUser");
            noHp = dataUser.getNoHp();
        }
        // Jika dari Login
        else if (fromWhere.equals("login")) {
            dataUser = getIntent().getParcelableExtra("dataUser");
            noHp = dataUser.getNoHp();
            isRememberMe = getIntent().getBooleanExtra("isRememberMe", false);
        }
        // Jika dari Forgot Password
        else if (fromWhere.equals("forgotPassword")) {
            noHp = getIntent().getStringExtra("noHp");
        }

        // On Click
        btnVerify.setOnClickListener(this);
        ivClose.setOnClickListener(this);

        tvNoHp.setText(noHp);

        sendOTPToUser(noHp);
    }

    private void sendOTPToUser(String noHp) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(noHp)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            codeBySystem = verificationId;
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String smsCode = phoneAuthCredential.getSmsCode();
            if (smsCode != null) {
                pinview.setText(smsCode);
                verifyCode(smsCode);
            }
        }
    };

    private void verifyCode(String smsCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, smsCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // Jika dari register
                        if (fromWhere.equals("register")) {
                            saveDataUserToDB();
                        }
                        // Jika dari login
                        else if (fromWhere.equals("login")) {
                            loginSession.createLoginSession(dataUser);
                            if (isRememberMe) {
                                rememberMeSession.createRememberMeSession();
                            }
                            Intent intent;
                            if (dataUser.getRole().equals("user")) {
                                intent = new Intent(this, DashboardActivity.class);
                            } else {
                                intent = new Intent(this, AdminDashboardActivity.class);
                            }
                            startActivity(intent);

                            finish();
                        }
                        // Jika dari Forgot Password
                        else if (fromWhere.equals("forgotPassword")) {
                            Intent intent = new Intent(this, NewPasswordActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            setSnackbar("Authentication Failed!");
                        }
                    }
                });
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

    private void saveDataUserToDB() {

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            firestoreRoot.document("users/" + uid).set(dataUser)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            loginSession.createLoginSession(dataUser);
                            addUserToStats();
                            Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            setSnackbar("Register Failed");
                        }
                    });
        }
    }

    private void addUserToStats() {
        firestoreRoot.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = firestoreRoot.document("stats/qty");
                DocumentSnapshot dataSnapshot = transaction.get(docRef);
                // Logic
                long newStat = dataSnapshot.getLong("user") + 1;
                transaction.update(docRef, "user", newStat);
                return null;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        if (btnId == R.id.btn_verify) {

            if (pinview.getText() != null) {
                if (!pinview.getText().toString().isEmpty()) {
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        setSnackbar("Mohon tunggu sebentar...");
                    } else {
                        String manualCode = pinview.getText().toString();
                        verifyCode(manualCode);
                    }

                } else  {
                    setSnackbar("Kode OTP tidak boleh kosong!");
                }

            }
        }

        if (btnId == R.id.iv_close_verify) {
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