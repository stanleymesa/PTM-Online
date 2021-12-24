package com.ptm.ppb_project.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.model.CartModel;

import java.text.SimpleDateFormat;

public class CartAdapter extends FirestoreRecyclerAdapter<CartModel, CartAdapter.CartViewHolder> {

    private OnItemClickCallback onItemClickCallback;

    public CartAdapter(@NonNull FirestoreRecyclerOptions<CartModel> options, OnItemClickCallback onItemClickCallback) {
        super(options);
        this.onItemClickCallback = onItemClickCallback;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMatpel;
        private final TextView tvKelas;
        private final TextView tvMateri;
        private final TextView tvWaktu;
        private final TextView tvCreatedAt;
        private final ImageView ivMinus;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMatpel = itemView.findViewById(R.id.tv_matpel_cart);
            tvKelas = itemView.findViewById(R.id.tv_kelas_cart);
            tvMateri = itemView.findViewById(R.id.tv_materi_cart);
            tvWaktu = itemView.findViewById(R.id.tv_waktu_cart);
            tvCreatedAt = itemView.findViewById(R.id.tv_createdAt);
            ivMinus = itemView.findViewById(R.id.iv_minus_cart);

        }
        public TextView getTvMatpel() {
            return tvMatpel;
        }

        public TextView getTvKelas() {
            return tvKelas;
        }

        public TextView getTvMateri() {
            return tvMateri;
        }

        public TextView getTvWaktu() {
            return tvWaktu;
        }

        public TextView getTvCreatedAt() {
            return tvCreatedAt;
        }

        public ImageView getIvMinus() {
            return ivMinus;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartModel model) {
        holder.getTvMatpel().setText(model.getMatpel());
        holder.getTvKelas().setText("Kelas " + model.getKelas());
        holder.getTvMateri().setText(model.getMateri());
        holder.getTvWaktu().setText(model.getHari() + ", " + convertToString(model.getStart_at()) + " - " + convertToString(model.getFinish_at()) + " WIB");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(model.getCreated_at());
        holder.getTvCreatedAt().setText(date);

        holder.getIvMinus().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemDeleteFromCart(model);
            }
        });
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_cart, parent, false);
        return new CartViewHolder(view);
    }

    private String convertToString(long waktu) {
        String waktuString = String.valueOf(waktu);

        // Menambahkan : waktuString
        StringBuilder after = new StringBuilder(waktuString);
        if (waktuString.length() == 3) {
            after.insert(1, ":");
        } else {
            after.insert(2, ":");
        }
        return after.toString();
    }

    public interface OnItemClickCallback {
        void onItemDeleteFromCart(CartModel dataCart);
    }



}
