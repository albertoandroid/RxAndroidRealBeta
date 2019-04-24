package com.androiddesdecero.rxusoreal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androiddesdecero.rxusoreal.R;
import com.androiddesdecero.rxusoreal.model.Contributor;
import com.androiddesdecero.rxusoreal.model.GitHubRepo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContributorAdapter extends RecyclerView.Adapter<ContributorAdapter.ContributorViewHolder> {

    private List<Contributor> contributors;

    public ContributorAdapter(List<Contributor> contributors){
        this.contributors = contributors;
    }

    @NonNull
    @Override
    public ContributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_tiem_contributor, parent, false);
        return new ContributorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContributorViewHolder holder, int position) {
        Contributor contributor = contributors.get(position);
        holder.tvName.setText(contributor.getLogin());
        holder.tvNumber.setText(contributor.getContributions()+"");
    }

    @Override
    public int getItemCount() {
        return contributors.size();
    }

    public void setData(List<Contributor> contributors){
        this.contributors = contributors;
        notifyDataSetChanged();
    }

    public class ContributorViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvNumber;

        public ContributorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvNumber = itemView.findViewById(R.id.tvNumber);
        }
    }
}
