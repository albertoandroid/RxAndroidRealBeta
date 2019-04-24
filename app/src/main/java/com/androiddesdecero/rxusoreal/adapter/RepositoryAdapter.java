package com.androiddesdecero.rxusoreal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androiddesdecero.rxusoreal.R;
import com.androiddesdecero.rxusoreal.model.GitHubRepo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder> {

    private List<GitHubRepo> repos;

    public RepositoryAdapter(List<GitHubRepo> repos){
        this.repos = repos;
    }


    @NonNull
    @Override
    public RepositoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_repo, parent, false);
        return new RepositoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RepositoryViewHolder holder, int position) {
        GitHubRepo repo = repos.get(position);
        holder.tvRepositorio.setText(repo.getName());
        holder.tvLenguaje.setText(repo.getLanguage());
        holder.tvStars.setText(repo.getStargazers_count()+"");
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }

    public void setData(List<GitHubRepo> repos){
        this.repos = repos;
        notifyDataSetChanged();
    }

    public class RepositoryViewHolder extends RecyclerView.ViewHolder{
       private TextView tvRepositorio;
       private TextView tvLenguaje;
       private TextView tvStars;

       public RepositoryViewHolder(@NonNull View itemView) {
           super(itemView);
           tvRepositorio = itemView.findViewById(R.id.tvRepositorio);
           tvLenguaje = itemView.findViewById(R.id.tvLenguaje);
           tvStars = itemView.findViewById(R.id.tvStars);
       }
   }
}
