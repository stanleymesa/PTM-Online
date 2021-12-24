package com.ptm.ppb_project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartModel implements Parcelable {
    private String matpel = "",
            materi = "",
            kelas = "",
            hari = "",
            id = "";

    private long start_at = 0,
            finish_at = 0,
            created_at = 0;

    public CartModel(PelajaranModel dataPelajaran, long currentTimeMillis) {
        setMatpel(dataPelajaran.getMatpel());
        setMateri(dataPelajaran.getMateri());
        setKelas(dataPelajaran.getKelas());
        setHari(dataPelajaran.getHari());
        setId(dataPelajaran.getId());
        setStart_at(dataPelajaran.getStart_at());
        setFinish_at(dataPelajaran.getFinish_at());
        setCreated_at(currentTimeMillis);
    }

    public CartModel() {}


    protected CartModel(Parcel in) {
        matpel = in.readString();
        materi = in.readString();
        kelas = in.readString();
        hari = in.readString();
        id = in.readString();
        start_at = in.readLong();
        finish_at = in.readLong();
        created_at = in.readLong();
    }

    public static final Creator<CartModel> CREATOR = new Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel in) {
            return new CartModel(in);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };

    public String getMatpel() {
        return matpel;
    }

    public void setMatpel(String matpel) {
        this.matpel = matpel;
    }

    public String getMateri() {
        return materi;
    }

    public void setMateri(String materi) {
        this.materi = materi;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public long getStart_at() {
        return start_at;
    }

    public void setStart_at(long start_at) {
        this.start_at = start_at;
    }

    public long getFinish_at() {
        return finish_at;
    }

    public void setFinish_at(long finish_at) {
        this.finish_at = finish_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(matpel);
        dest.writeString(materi);
        dest.writeString(kelas);
        dest.writeString(hari);
        dest.writeString(id);
        dest.writeLong(start_at);
        dest.writeLong(finish_at);
        dest.writeLong(created_at);
    }
}
