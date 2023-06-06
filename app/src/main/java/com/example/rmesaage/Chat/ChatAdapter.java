package com.example.rmesaage.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;

import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public List<Message> messageList;
    private String currentUser;
    private Context context;

    public ChatAdapter(List<Message> messageList, String currentUser,Context context) {
        this.messageList = messageList;
        this.currentUser = currentUser;
        this.context = context;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 0) {
            // Инфлейт макета для сообщения
            view = inflater.inflate(R.layout.item_message, parent, false);
        } else {
            // Инфлейт макета для изображения
            view = inflater.inflate(R.layout.item_message, parent, false);
        }
        return new ChatViewHolder(view);
    }

    private void openImageInSystemApp(byte[] bitmapData) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        Uri imageUri = Uri.parse(path);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(imageUri, "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private String getMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message, position);

        // Добавить слушатель кликов
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getPaths()!= null) {
                    if (message.getPaths().size() == 1) {
                        // Открыть документ в системном приложении
                        String filePath = message.getPaths().get(0);
                        System.out.println(filePath);
                        openDocumentInSystemApp(filePath);
                    } else if (message.getBitMaps() != null) {
                        // Открыть изображение в системном приложении
                        int bitmapIndex = holder.getAdapterPosition();
                        if (bitmapIndex >= 0 && bitmapIndex < message.getBitMaps().size()) {
                            byte[] bitmapData = message.getBitMaps().get(bitmapIndex);
                            openImageInSystemApp(bitmapData);
                        }
                    }
                }
            }
        });
    }

    private void openDocumentInSystemApp(String filePath) {
        File file = new File(filePath);
        Uri fileUri = FileProvider.getUriForFile(context, "com.example.rmesaage.fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, getMimeType(filePath));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getBitMaps() != null) {
            // Тип элемента - изображение
            return 1;
        } else {
            // Тип элемента - сообщение
            return 0;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;
        private RecyclerView recyclerView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.text_view_message);
            recyclerView = itemView.findViewById(R.id.rv_media_list);
        }
        //TODO РАЗОБРАТЬСЯ!!!!!
        public void bind(Message message, int position) {
            System.out.println(message.getBitMaps());
            //Вставка того, что имеет битмапы
            if (message.getBitMaps() != null) {
                        textViewMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        List<Bitmap> medias = new ArrayList<>();
                        int[] scale;
                        for (int i = 0; i < message.getBitMaps().size(); i++) {
                            medias.add(BitmapFactory.decodeByteArray(message.getBitMaps().get(i), 0, message.getBitMaps().get(i).length));
                        }
                        if (message.getBitMaps().size() == 1) {
                            GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 1);
                            recyclerView.setLayoutManager(layoutManager);
                            scale = new int[]{900, 700};
                            MediaAdapter mediaAdapter = new MediaAdapter(medias, scale);
                            recyclerView.setAdapter(mediaAdapter);
                        } else if (message.getBitMaps().size() >= 2 && message.getBitMaps().size() < 4) {
                            GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
                            recyclerView.setLayoutManager(layoutManager);
                            scale = new int[]{800, 700};
                            MediaAdapter mediaAdapter = new MediaAdapter(medias, scale);
                            recyclerView.setAdapter(mediaAdapter);
                        } else if (message.getBitMaps().size() >= 4 && message.getBitMaps().size() < 6) {
                            GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 3);
                            recyclerView.setLayoutManager(layoutManager);
                            scale = new int[]{700, 600};
                            MediaAdapter mediaAdapter = new MediaAdapter(medias, scale);
                            recyclerView.setAdapter(mediaAdapter);
                        } else {
                            GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 4);
                            recyclerView.setLayoutManager(layoutManager);
                            scale = new int[]{600, 500};
                            MediaAdapter mediaAdapter = new MediaAdapter(medias, scale);
                            recyclerView.setAdapter(mediaAdapter);
                        }
                    }else {
                    textViewMessage.setVisibility(View.VISIBLE);
                    textViewMessage.setText(message.getText());
                    if (message.getMessageUser().equals(currentUser)) {
                        // Сообщение пользователя
                        textViewMessage.setBackgroundResource(R.drawable.message_bubble_user);
                        textViewMessage.setGravity(Gravity.END);
                    } else {
                        // Сообщение собеседника
                        textViewMessage.setBackgroundResource(R.drawable.message_bubble_other);
                        textViewMessage.setGravity(Gravity.START);
                    }
                }
            }
        }
        public boolean insert(Message message) {
            messageList.add(message);
            return true;
    }
}
