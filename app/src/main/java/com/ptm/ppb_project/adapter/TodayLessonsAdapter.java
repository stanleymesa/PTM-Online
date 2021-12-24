package com.ptm.ppb_project.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptm.ppb_project.R;
import com.ptm.ppb_project.model.PelajaranModel;

import java.util.ArrayList;

public class TodayLessonsAdapter extends RecyclerView.Adapter<TodayLessonsAdapter.TodayViewHolder> {

    private ArrayList<PelajaranModel> list;
    private OnShowMoreClickback onShowMoreClickback;

    public TodayLessonsAdapter(ArrayList<PelajaranModel> list, OnShowMoreClickback onShowMoreClickback) {
        this.list = list;
        this.onShowMoreClickback = onShowMoreClickback;
    }

    public static class TodayViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvKelas;
        private final TextView tvMateri;
        private final TextView tvWaktu;
        private final TextView tvShowMore;

        public TodayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKelas = itemView.findViewById(R.id.tv_kelas_today);
            tvMateri = itemView.findViewById(R.id.tv_materi_today);
            tvWaktu = itemView.findViewById(R.id.tv_waktu_today);
            tvShowMore = itemView.findViewById(R.id.tv_showmore);
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

        public TextView getTvShowMore() {
            return tvShowMore;
        }
    }


    @NonNull
    @Override
    public TodayLessonsAdapter.TodayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_todaylessons_admin, parent, false);
        return new TodayViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TodayLessonsAdapter.TodayViewHolder holder, int position) {
        PelajaranModel data = list.get(position);
        holder.getTvKelas().setText(data.getMatpel() + " kelas " + data.getKelas());
        holder.getTvMateri().setText(data.getMateri());
        holder.getTvWaktu().setText(data.getHari() + ", " + convertToString(data.getStart_at()) + " - " + convertToString(data.getFinish_at()) + " WIB");

        // Logic Show More
        if (getItemCount() % 3 == 0) {
            if (position == getItemCount()-1) {
                holder.getTvShowMore().setVisibility(View.VISIBLE);
            }
        } else {
            holder.getTvShowMore().setVisibility(View.GONE);
        }


        holder.getTvShowMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowMoreClickback.onShowMoreClicked(holder.getTvShowMore());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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

    public interface OnShowMoreClickback {
        void onShowMoreClicked(TextView tvShowMore);
    }


}
