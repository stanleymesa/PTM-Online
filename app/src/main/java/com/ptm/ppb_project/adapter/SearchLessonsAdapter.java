package com.ptm.ppb_project.adapter;

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
import com.ptm.ppb_project.model.PelajaranModel;

public class SearchLessonsAdapter extends FirestoreRecyclerAdapter<PelajaranModel, SearchLessonsAdapter.SearchLessonsViewHolder> {

    public SearchLessonsAdapter(@NonNull FirestoreRecyclerOptions<PelajaranModel> options) {
        super(options);
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
        private final ImageView ivEdit;
        private final ImageView ivDelete;

        public SearchLessonsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMatpel = itemView.findViewById(R.id.tv_matpel_search);
            tvKelas = itemView.findViewById(R.id.tv_kelas_search);
            tvMateri = itemView.findViewById(R.id.tv_materi_search);
            tvWaktu = itemView.findViewById(R.id.tv_waktu_search);
            ivEdit = itemView.findViewById(R.id.iv_edit_search);
            ivDelete = itemView.findViewById(R.id.iv_delete_search);
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

        public ImageView getIvEdit() {
            return ivEdit;
        }

        public ImageView getIvDelete() {
            return ivDelete;
        }
    }


    @Override
    protected void onBindViewHolder(@NonNull SearchLessonsViewHolder holder, int position, @NonNull PelajaranModel model) {

    }

}
