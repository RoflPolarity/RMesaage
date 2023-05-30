package com.example.rmesaage.Chat.MediaPicker;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.Chat.MediaPicker.ImageItem;
import com.example.rmesaage.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> implements ListAdapter {
    private List<ImageItem> imageItems;
    private Context context;
    private DataSetObserver dataSetObserver;
    private static final int VIEW_TYPE_COUNT = 1;

    int thresholdPosition;

    public ImageAdapter(List<ImageItem> imageItems, Context context) {
        this.imageItems = imageItems;
        this.context = context;
    }

    public void setData(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
        thresholdPosition = imageItems.size()/4;
        notifyDataSetChanged();
    }

    public ImageItem getItem(int position) {
        return imageItems.get(position);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Return the item view for the GridView
        ImageViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            holder = new ImageViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ImageViewHolder) convertView.getTag();
        }
        ImageItem imageItem = imageItems.get(position);

        Uri imageUri = Uri.parse(imageItem.getPath());
        Picasso.get().load(imageUri).resize(250, 250).into(holder.imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

        return convertView;
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return ListAdapter.super.getAutofillOptions();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObserver = observer;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObserver = null;
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    private void notifyDataSetChangedObserver() {
        if (dataSetObserver != null) {
            dataSetObserver.onChanged();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
