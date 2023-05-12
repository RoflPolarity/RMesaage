package com.example.rmesaage.Chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public List<Message> messageList;
    private String currentUser;

    public ChatAdapter(List<Message> messageList, String currentUser) {
        this.messageList = messageList;
        this.currentUser = currentUser;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;
        private RecyclerView recyclerView;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.text_view_message);
            recyclerView = itemView.findViewById(R.id.rv_media_list);
        }

        public void bind(Message message) {
            if (message.getBitMaps()!=null){
                textViewMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                List<Bitmap> medias = new ArrayList<>();
                for (int i = 0; i < message.getBitMaps().size(); i++) {
                    medias.add(BitmapFactory.decodeByteArray(message.getBitMaps().get(i),0,message.getBitMaps().get(i).length));
                }

                MediaAdapter mediaAdapter = new MediaAdapter(medias);
                recyclerView.setAdapter(mediaAdapter);
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


    public boolean updateDialog(ArrayList<Message> messages){
        messageList.clear();
        this.messageList = messages;
        notifyDataSetChanged();
        return true;
    }

    public boolean insert(Message message){

        messageList.add(message);
        System.out.println(messageList.size());
        System.out.println(messageList.get(messageList.size()-1).getBitMaps());
        notifyDataSetChanged();
        return true;
    }
}
