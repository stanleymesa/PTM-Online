package com.ptm.ppb_project.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.ptm.ppb_project.model.UserModel;

public class SessionManager {

    SharedPreferences myPref;
    SharedPreferences.Editor editor;

    // Session Type
    public static String LOGIN_SESSION = "loginSession";
    public static String REMEMBERME_SESSION = "rememberMeSession";
    public static String EDIT_LESSONS_SESSION = "editLessonsSession";
    public static String ADD_LESSONS_SESSION = "addLessonsSession";
    public static String UPDATE_PASSWORD_SESSION = "updatePasswordSession";

    // User Session
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_KELAS = "kelas";
    private static final String KEY_NAMAKELAS = "namaKelas";
    private static final String KEY_ABSEN = "absen";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NOHP = "noHp";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TGL_REGISTER = "tgl_register";

    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String IS_REMEMBER_ME = "isRememberMe";

    // Edit Lessons Session
    private static final String IS_EDIT_SUCCESS = "isEditSuccess";

    // Add Lessons Session
    private static final String IS_ADD_SUCCESS = "isAddSuccess";

    // Update Password Session
    private static final String IS_PASSWORD_UPDATED = "isPasswordUpdated";
    private static final String IS_UPDATE_PASSWORD_SUCCESS = "isUpdatePasswordSuccess";

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

    // Edit Lessons Session
    public void createEditLessonsSession(boolean isSuccess) {
        editor.clear();
        editor.putBoolean(IS_EDIT_SUCCESS, isSuccess);
        editor.apply();
    }

    public boolean isEditLessonsSuccess() {
        return myPref.getBoolean(IS_EDIT_SUCCESS, false);
    }

    public void clearEditLessonsSession() {
        editor.clear().apply();
    }

    // Add Lessons Session
    public void createAddLessonsSession(boolean isSuccess) {
        editor.clear();
        editor.putBoolean(IS_ADD_SUCCESS, isSuccess);
        editor.apply();
    }

    public boolean isAddLessonsSuccess() {
        return myPref.getBoolean(IS_ADD_SUCCESS, false);
    }

    public void clearAddLessonsSession() {
        editor.clear().apply();
    }

    // Update Password Session
    public void createUpdatePasswordSession(boolean isUpdated, boolean isSuccess) {
        editor.clear();
        editor.putBoolean(IS_PASSWORD_UPDATED, isUpdated);
        editor.putBoolean(IS_UPDATE_PASSWORD_SUCCESS, isSuccess);
        editor.apply();
    }

    public boolean isPasswordUpdated() {
        return myPref.getBoolean(IS_PASSWORD_UPDATED, false);
    }

    public boolean isUpdatePasswordSuccess() {
        return myPref.getBoolean(IS_UPDATE_PASSWORD_SUCCESS, false);
    }

    public void clearUpdatePasswordSession() {
        editor.clear().apply();
    }
}
