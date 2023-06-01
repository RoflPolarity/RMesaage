package com.example.rmesaage.Chat.MediaPicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.rmesaage.Chat.UserChat;
import com.example.rmesaage.R;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImagePickerActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    public static final String EXTRA_SELECTED_IMAGES = "selected_images";

    private HashSet<Uri> selected = new HashSet<>();

    private ImageAdapter imageAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        GridView gridView = findViewById(R.id.grid_view_images);
        Button btnSend = findViewById(R.id.button_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создание множества выбранных изображений
                Intent resultIntent = new Intent();
                resultIntent.putExtra(ImagePickerActivity.EXTRA_SELECTED_IMAGES, createTempImageFile(selected).toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        Button btnCancel = findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        imageAdapter = new ImageAdapter(new ArrayList<>(), this);
        gridView.setAdapter(imageAdapter);
        // Проверка разрешения на чтение внешнего хранилища
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Запрос разрешения
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Разрешение уже предоставлено, загружаем изображения
            loadImages();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Разрешение не предоставлено, запросите его у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            System.out.println("Разрешение получено");
        }else {
            File file = new File("/data/data/com.example.rmesaage/files/Temp.txt");
            try {
                file.createNewFile();
                System.out.println("Создано");
            } catch (IOException e) {
                File file1 = new File("/data/data/com.example.rmesaage/files");
                file1.mkdir();
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }




        // Обработка клика на изображение
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ImageItem imageItem = imageAdapter.getItem(position);
                Uri imageUri = Uri.parse(imageItem.getPath());
                    if (selected.contains(imageUri)) {
                        selected.remove(imageUri);
                    } else {
                        selected.add(imageUri);
                    }
            }

        });
    }

    // Загрузка изображений из внешнего хранилища
    private void loadImages() {
        String[] projection = {MediaStore.Images.Media._ID};

        try (Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)) {
            if (cursor != null) {
                List<ImageItem> imageItems = new ArrayList<>();

                while (cursor.moveToNext()) {
                    long imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
                    ImageItem imageItem = new ImageItem(imageUri.toString());
                    imageItems.add(imageItem);
                }

                imageAdapter.setData(imageItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE || requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, выполните необходимые действия

                if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
                    // Разрешение на чтение внешнего хранилища предоставлено, загрузите изображения
                    loadImages();
                } else if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
                    // Разрешение на запись внешнего хранилища предоставлено, выполните соответствующие действия
                    // например, запись во внешнее хранилище
                }
            } else {
                // Разрешение не предоставлено, обработайте эту ситуацию
                finish();
            }
        }
    }

    public Path createTempImageFile(Set<Uri> select){
        File file = new File("/data/data/com.example.rmesaage/files/Temp.txt");
        try {
            FileWriter fr = new FileWriter(file);
            BufferedWriter br = new BufferedWriter(fr);
            select.forEach(x->{
                try {
                    br.write(x.toString()+"\n");
                    System.out.println(x);
                    br.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        return Paths.get(file.getAbsolutePath());
    }
}
