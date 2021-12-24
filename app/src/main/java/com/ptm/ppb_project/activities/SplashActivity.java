package com.ptm.ppb_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.admin.AdminDashboardActivity;
import com.ptm.ppb_project.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    ImageView ivLogo;
    Animation logoAnim;
    FirebaseAuth mAuth;
    SessionManager rememberMeSession, loginSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hooks
        ivLogo = findViewById(R.id.iv_logo_splash);
        Glide.with(this)
                .load(R.drawable.tt)
                .into(ivLogo);
        logoAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_anim);
        ivLogo.setAnimation(logoAnim);

        // Set Firebase
        mAuth = FirebaseAuth.getInstance();

        // Set Session
        loginSession = new SessionManager(this, SessionManager.LOGIN_SESSION);
        rememberMeSession = new SessionManager(this, SessionManager.REMEMBERME_SESSION);

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Jika Remember Me
                if (rememberMeSession.isRememberedMe()) {
                    Intent intent;
                    if (loginSession.getLoginSessionData().getRole().equals("user")) {
                        intent = new Intent(getBaseContext(), DashboardActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getBaseContext(), AdminDashboardActivity.class);
                        startActivity(intent);
                    }
                    finish();

                } else {
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };

        handler.postDelayed(runnable, 4000);

    }

}