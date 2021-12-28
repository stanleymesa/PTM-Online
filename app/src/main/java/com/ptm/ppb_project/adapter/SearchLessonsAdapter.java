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
import com.ptm.ppb_project.model.PelajaranModel;

import java.util.ArrayList;

public class SearchLessonsAdapter extends RecyclerView.Adapter<SearchLessonsAdapter.SearchLessonsViewHolder> {
    private ArrayList<PelajaranModel> listData;
    private OnItemClickCallback onItemClickCallback;

    public SearchLessonsAdapter(ArrayList<PelajaranModel> listData, OnItemClickCallback onItemClickCallback) {
        this.listData = listData;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public SearchLessonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_search_lessons, parent, false);
        return new SearchLessonsViewHolder(view);
    }

    public class SearchLessonsViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMatpel;
        private final TextView tvKelas;
        private final TextView tvMateri;
        private final TextView tvWaktu;
        private final TextView tvShowMore;
        private final ImageView ivEdit;
        private final ImageView ivDelete;
        private final Space space;

        public SearchLessonsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMatpel = itemView.findViewById(R.id.tv_matpel_search);
            tvKelas = itemView.findViewById(R.id.tv_kelas_search);
            tvMateri = itemView.findViewById(R.id.tv_materi_search);
            tvWaktu = itemView.findViewById(R.id.tv_waktu_search);
            tvShowMore = itemView.findViewById(R.id.tv_showmore_search);
            ivEdit = itemView.findViewById(R.id.iv_edit_search);
            ivDelete = itemView.findViewById(R.id.iv_delete_search);
            space = itemView.findViewById(R.id.space_search);
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

        public TextView getTvShowMore() {
            return tvShowMore;
        }

        public ImageView getIvEdit() {
            return ivEdit;
        }

        public ImageView getIvDelete() {
            return ivDelete;
        }

        public Space getSpace() {
            return space;
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SearchLessonsViewHolder holder, int position) {
        PelajaranModel data = listData.get(position);
        holder.getTvKelas().setText("Kelas " + data.getKelas());
        holder.getTvMatpel().setText(data.getMatpel());
        holder.getTvMateri().setText(data.getMateri());
        holder.getTvWaktu().setText(data.getHari() + ", " + convertToString(data.getStart_at()) + " - " + convertToString(data.getFinish_at()) + " WIB");

        // Logic Show More
        if (getItemCount() % 5 == 0) {
            if (position == getItemCount()-1) {
                holder.getTvShowMore().setVisibility(View.VISIBLE);
            }
        } else {
            holder.getTvShowMore().setVisibility(View.GONE);
        }

        // Space
        holder.getSpace().setVisibility(View.GONE);
        if (position == getItemCount() - 1) {
            holder.getSpace().setVisibility(View.VISIBLE);
        }

        // Interface
        holder.getTvShowMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onShowMoreClick(holder.getTvShowMore());
            }
        });

        holder.getIvEdit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onEditLessons(data);
            }
        });

        holder.getIvDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onDeleteLessons(data);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
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
        void onShowMoreClick(TextView tvShowMore);
        void onEditLessons(PelajaranModel pelajaranModel);
        void onDeleteLessons(PelajaranModel pelajaranModel);
    }

}
