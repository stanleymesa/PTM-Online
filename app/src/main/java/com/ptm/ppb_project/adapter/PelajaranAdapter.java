package com.ptm.ppb_project.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ptm.ppb_project.R;
import com.ptm.ppb_project.model.CartModel;
import com.ptm.ppb_project.model.PelajaranModel;

import java.util.ArrayList;

public class PelajaranAdapter extends RecyclerView.Adapter<PelajaranAdapter.PelajaranViewHolder> {

    private OnItemClickCallback onItemClickCallback;
    private ArrayList<PelajaranModel> dataPelajaran;
    private ArrayList<PelajaranModel> dataCart;

    public PelajaranAdapter(
            OnItemClickCallback onItemClickCallback,
            ArrayList<PelajaranModel> dataPelajaran,
            ArrayList<PelajaranModel> dataCart
    ) {
        this.onItemClickCallback = onItemClickCallback;
        this.dataPelajaran = dataPelajaran;
        this.dataCart = dataCart;
    }

    // INISIALISASI & DEFINISI
    public static class PelajaranViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvKelas;
        private final TextView tvMateri;
        private final TextView tvWaktu;
        private final TextView tvBentrokTitle;
        private final TextView tvBentrokMateri;
        private final TextView tvKuota;
        private final ImageView ivPlus;
        private final ImageView ivMinus;
        private final Space space;

        public PelajaranViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKelas = itemView.findViewById(R.id.tv_kelas);
            tvMateri = itemView.findViewById(R.id.tv_materi);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            tvBentrokTitle = itemView.findViewById(R.id.tv_bentrok_title);
            tvBentrokMateri = itemView.findViewById(R.id.tv_bentrok_materi);
            ivPlus = itemView.findViewById(R.id.iv_plus);
            ivMinus = itemView.findViewById(R.id.iv_minus);
            tvKuota = itemView.findViewById(R.id.tv_kuota);
            space = itemView.findViewById(R.id.space_pelajaran);
        }

        public Space getSpace() {
            return space;
        }

        public TextView getTvKuota() {
            return tvKuota;
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

        public TextView getTvBentrokTitle() {
            return tvBentrokTitle;
        }

        public TextView getTvBentrokMateri() {
            return tvBentrokMateri;
        }

        public ImageView getIvPlus() {
            return ivPlus;
        }

        public ImageView getIvMinus() {
            return ivMinus;
        }
    }

    // MEMASUKKAN LAYOUT ITEM ROW KE ADAPTER
    @NonNull
    @Override
    public PelajaranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_pelajaran, parent, false);
        return new PelajaranViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PelajaranViewHolder holder, int position) {
        PelajaranModel model = dataPelajaran.get(position);

        holder.getTvKelas().setText("Kelas " + model.getKelas());
        holder.getTvMateri().setText(model.getMateri());

        holder.getTvWaktu().setText(model.getHari() + ", " + convertToString(model.getStart_at()) + " - " + convertToString(model.getFinish_at()) + " WIB");
        holder.getTvKuota().setText(String.valueOf(model.getKuota()));

        holder.getTvBentrokTitle().setVisibility(View.GONE);
        holder.getTvBentrokMateri().setVisibility(View.GONE);

        // On Item Click Callback
        holder.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemAddToCart(model);
            }
        });

        holder.ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemDeleteFromCart(model);
            }
        });

        // Logic Button
        holder.ivMinus.setVisibility(View.GONE);

        // Jika Kuota habis, plus hilang
        if (model.getKuota() <= 0) {
            holder.ivPlus.setVisibility(View.GONE);
        }


        for (PelajaranModel myCart : dataCart) {
            // Jika Matpel sudah ada di cart, plus hilang, minus ada
            if (model.getId().equals(myCart.getId())) {
                holder.ivPlus.setVisibility(View.GONE);
                holder.ivMinus.setVisibility(View.VISIBLE);
            }


            // Bentrok Logic
            if (myCart.getHari().equals(model.getHari())) {
                if (
                        (model.getStart_at() >= myCart.getStart_at() && model.getStart_at() <= myCart.getFinish_at()) ||
                                (model.getFinish_at() >= myCart.getStart_at() && model.getFinish_at() <= myCart.getFinish_at())
                ) {
                    holder.ivPlus.setVisibility(View.GONE);
                    holder.getTvBentrokTitle().setVisibility(View.VISIBLE);
                    holder.getTvBentrokMateri().setVisibility(View.VISIBLE);
                    holder.getTvBentrokMateri().setText(myCart.getMatpel() + " " + myCart.getMateri() + " " + convertToString(myCart.getStart_at()) + " - " + convertToString(myCart.getFinish_at()) + " WIB");
                }
            }
            // End Bentrok Logic

        }
        // End Logic Button



        // Space at last item
        if (position == getItemCount() - 1) {
            holder.space.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataPelajaran.size();
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
        void onItemAddToCart(PelajaranModel dataPelajaran);
        void onItemDeleteFromCart(PelajaranModel dataPelajaran);
    }


}
