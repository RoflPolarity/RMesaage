package com.example.rmesaage.Chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private List<Bitmap> mediaList;
    private int[] scales;

    public MediaAdapter(List<Bitmap> mediaList,int[] scales) {
        this.mediaList = mediaList;
        this.scales = scales;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Bitmap media = mediaList.get(position);
        holder.bind(media,scales);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        private ImageView mediaView;


        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mediaView = itemView.findViewById(R.id.MediaView);
        }

        public void bind(Bitmap media,int[] scales) {
            
            mediaView.setVisibility(View.VISIBLE);
            mediaView.setImageBitmap(getResizedBitmap(media,scales));

        }

        public Bitmap getResizedBitmap(Bitmap bitmap,int[] scales) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = scales[0];
            int newHeight = scales[1];
            float aspectRatio = (float) newWidth / (float) newHeight;
            float originalAspectRatio = (float) width / (float) height;
            if (originalAspectRatio > aspectRatio) {
                newHeight = (int) (newWidth / originalAspectRatio);
            } else {
                newWidth = (int) (newHeight * originalAspectRatio);
            }
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        }
    }
}
