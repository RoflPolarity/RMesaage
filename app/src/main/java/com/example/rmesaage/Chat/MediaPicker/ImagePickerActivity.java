package com.example.rmesaage.Chat.MediaPicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.rmesaage.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    public static final String EXTRA_SELECTED_DOCS = "selected_docs";

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
                Path tempFilePath = createTempImageFile(selected);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_SELECTED_IMAGES, tempFilePath.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        Button btnCancel = findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        imageAdapter = new ImageAdapter(new ArrayList<>(), this);
        gridView.setAdapter(imageAdapter);

        ConstraintLayout imageViewImages = findViewById(R.id.roundButtonImage);
        imageViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setAdapter(imageAdapter);
            }
        });

        ConstraintLayout imageViewDocs = findViewById(R.id.roundButtonDoc);
        ActivityResultLauncher<String> documentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            selected.clear();
                            selected.add(result);
                            Path tempFilePath = createTempImageFile(selected);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(EXTRA_SELECTED_DOCS, tempFilePath.toString());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }
                });

        imageViewDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentLauncher.launch("*/*");
            }
        });

        // Проверка разрешения на чтение внешнего хранилища
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            loadImages();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        } else {
            createTempImageFile(selected);
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
                imageAdapter.notifyDataSetChanged(); // Обновление состояния адаптера
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

    public Path createTempImageFile(Set<Uri> select) {
        File file = new File(getFilesDir(), "Temp.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (Uri uri : select) {
                bufferedWriter.write(uri.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        return file.toPath();
    }
}
