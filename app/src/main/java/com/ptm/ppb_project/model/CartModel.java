package com.ptm.ppb_project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartModel implements Parcelable {

    private long created_at = 0;

    public CartModel(long currentTimeMillis) {
        setCreated_at(currentTimeMillis);
    }

    public CartModel() {}


    protected CartModel(Parcel in) {
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

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(created_at);
    }
}
