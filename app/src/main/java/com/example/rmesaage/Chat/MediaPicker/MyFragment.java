package com.example.rmesaage.Chat.MediaPicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;
import com.example.rmesaage.utils.OnImageSelectedListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyFragment extends Fragment {

    private OnImageSelectedListener mListener;

    private Md_adapter adapter;
    private static FrameLayout frame;
    public MyFragment() {
        // Пустой конструктор обязателен
    }

    public static MyFragment newInstance(Md_adapter adapter, FrameLayout frameLayout) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putSerializable("adapter", adapter);
        fragment.setArguments(args);
        frame = frameLayout;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            adapter = (Md_adapter) getArguments().getSerializable("adapter");
        }

    }
    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_media_choose, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(adapter);
        ImageButton button = view.findViewById(R.id.button_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frame.getVisibility()==View.VISIBLE) frame.setVisibility(View.GONE);
            }
        });
        ImageButton apply = view.findViewById(R.id.button_select);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Md_adapter.ViewHolder> arrayList = adapter.selected;
                ArrayList<String> selectedImagePaths = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++)selectedImagePaths.add(arrayList.get(i).imagePath);
                if (mListener != null) {
                    mListener.onImageSelected(selectedImagePaths);
                }
            }
        });
        return view;
    }
}
