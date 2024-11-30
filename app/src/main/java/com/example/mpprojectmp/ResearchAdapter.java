package com.example.mpprojectmp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResearchAdapter extends RecyclerView.Adapter<ResearchAdapter.ViewHolder> {

    private List<String> researchList;
    private Context context;

    public ResearchAdapter(Context context, List<String> researchList) {
        this.context = context;
        this.researchList = researchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_research, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String research = researchList.get(position);
        holder.researchTitle.setText(research);

        holder.shareIcon.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, research);
            context.startActivity(Intent.createChooser(shareIntent, "Share Research via"));
        });

    }

    @Override
    public int getItemCount() {
        return researchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView researchTitle;
        ImageView shareIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            researchTitle = itemView.findViewById(R.id.researchTitle);
            shareIcon = itemView.findViewById(R.id.shareIcon);
        }
    }
}
