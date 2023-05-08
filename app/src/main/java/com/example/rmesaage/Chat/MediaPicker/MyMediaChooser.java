package com.example.rmesaage.Chat.MediaPicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rmesaage.R;

public class MyMediaChooser extends Fragment {

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_media_choose, container, false);
        // инициализация RecyclerView, кнопок и прочих элементов интерфейса

        return rootView;
    }

}
