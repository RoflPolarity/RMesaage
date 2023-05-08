package com.example.rmesaage.Chat.MediaPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rmesaage.R;

import java.io.File;
import java.util.List;

public class Md_adapter extends RecyclerView.Adapter<Md_adapter.ViewHolder> {

    private List<String> dataList;
    private Context context;

    public Md_adapter(List<String> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_media_choose, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = dataList.get(position);
        Glide.with(context).load(new File(path)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
