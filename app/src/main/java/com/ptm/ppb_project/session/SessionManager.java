package com.ptm.ppb_project.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.ptm.ppb_project.model.UserModel;

public class SessionManager {

    SharedPreferences myPref;
    SharedPreferences.Editor editor;

    public static String LOGIN_SESSION = "loginSession";
    public static String REMEMBERME_SESSION = "rememberMeSession";

    public static String KEY_FULLNAME = "fullname";
    public static String KEY_KELAS = "kelas";
    public static String KEY_NAMAKELAS = "namaKelas";
    public static String KEY_ABSEN = "absen";
    public static String KEY_EMAIL = "email";
    public static String KEY_NOHP = "noHp";
    public static String KEY_PASSWORD = "password";
    public static String KEY_ROLE = "role";
    public static String KEY_TGL_REGISTER = "tgl_register";

    private static String IS_LOGGED_IN = "isLoggedIn";
    private static String IS_REMEMBER_ME = "isRememberMe";

    public SessionManager(Context context, String sessionType) {
        myPref = context.getSharedPreferences(sessionType, Context.MODE_PRIVATE);
        editor = myPref.edit();
    }

    // Login Session
    public void createLoginSession(UserModel dataUser) {
        editor.clear();
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_FULLNAME, dataUser.getFullname());
        editor.putString(KEY_KELAS, dataUser.getKelas());
        editor.putString(KEY_NAMAKELAS, dataUser.getNamaKelas());
        editor.putString(KEY_ABSEN, dataUser.getAbsen());
        editor.putString(KEY_EMAIL, dataUser.getEmail());
        editor.putString(KEY_NOHP, dataUser.getNoHp());
        editor.putString(KEY_PASSWORD, dataUser.getPassword());
        editor.putString(KEY_ROLE, dataUser.getRole());
        editor.putString(KEY_TGL_REGISTER, dataUser.getTgl_register());
        editor.apply();
    }

    public UserModel getLoginSessionData() {
        UserModel dataUser = new UserModel();
        dataUser.setFullname(myPref.getString(KEY_FULLNAME, ""));
        dataUser.setKelas(myPref.getString(KEY_KELAS, ""));
        dataUser.setNamaKelas(myPref.getString(KEY_NAMAKELAS, ""));
        dataUser.setAbsen(myPref.getString(KEY_ABSEN, ""));
        dataUser.setEmail(myPref.getString(KEY_EMAIL, ""));
        dataUser.setNoHp(myPref.getString(KEY_NOHP, ""));
        dataUser.setPassword(myPref.getString(KEY_PASSWORD, ""));
        dataUser.setRole(myPref.getString(KEY_ROLE, "user"));
        dataUser.setTgl_register(myPref.getString(KEY_TGL_REGISTER, ""));
        return dataUser;
    }

    public boolean isLoggedIn() {
        return myPref.getBoolean(IS_LOGGED_IN, false);
    }

    public void clearLoginSession() {
        editor.clear().apply();
    }

    // Remember Me Session
    public void createRememberMeSession() {
        editor.clear();
        editor.putBoolean(IS_REMEMBER_ME, true);
        editor.apply();
    }

    public void clearRememberMeSession() {
        editor.clear().apply();
    }

    public boolean isRememberedMe() {
        return myPref.getBoolean(IS_REMEMBER_ME, false);
    }
}
