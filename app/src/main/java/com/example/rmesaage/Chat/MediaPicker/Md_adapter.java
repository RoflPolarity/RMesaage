package com.example.rmesaage.Chat.MediaPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Md_adapter extends RecyclerView.Adapter<Md_adapter.ViewHolder> implements Serializable {

    private List<String> dataList;
    private Context context;
    public ArrayList<ViewHolder> selected = new ArrayList<>();

    public Md_adapter(List<String> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<String> getDataList() {
        return dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imagePath = dataList.get(position);
        holder.bind();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected.add(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public String imagePath;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.MediaView);
        }
        private void bind(){
            if (imagePath != null && new File(imagePath).canRead()) {
                Picasso.get().load(new File(imagePath))
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.broken)
                        .resize(250,250)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
}
