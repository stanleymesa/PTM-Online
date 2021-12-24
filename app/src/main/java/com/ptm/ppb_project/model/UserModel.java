package com.ptm.ppb_project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    private String fullname = "",
            kelas = "",
            namaKelas = "",
            absen = "",
            email = "",
            noHp = "",
            password = "",
            role = "",
            tgl_register = "";

    public UserModel() {}

    public UserModel(
            String fullname,
            String kelas,
            String namaKelas,
            String absen,
            String email,
            String noHp,
            String password,
            String role,
            String tgl_register
    ) {
        setFullname(fullname);
        setKelas(kelas);
        setNamaKelas(namaKelas);
        setAbsen(absen);
        setEmail(email);
        setNoHp(noHp);
        setPassword(password);
        setRole(role);
        setTgl_register(tgl_register);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    protected UserModel(Parcel in) {
        fullname = in.readString();
        kelas = in.readString();
        namaKelas = in.readString();
        absen = in.readString();
        email = in.readString();
        noHp = in.readString();
        password = in.readString();
        tgl_register = in.readString();
        role = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getAbsen() {
        return absen;
    }

    public void setAbsen(String absen) {
        this.absen = absen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTgl_register() {
        return tgl_register;
    }

    public void setTgl_register(String tgl_register) {
        this.tgl_register = tgl_register;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullname);
        dest.writeString(kelas);
        dest.writeString(namaKelas);
        dest.writeString(absen);
        dest.writeString(email);
        dest.writeString(noHp);
        dest.writeString(password);
        dest.writeString(tgl_register);
        dest.writeString(role);
    }
}
