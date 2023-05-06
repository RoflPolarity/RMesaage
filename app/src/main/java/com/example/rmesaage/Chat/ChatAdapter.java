package com.example.rmesaage.Chat;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;
import com.example.rmesaage.utils.server_utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public List<Message> messageList;
    private String currentUser;

    public ChatAdapter(List<Message> messageList, String currentUser,String sendTo) {
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
        private TextView name;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.text_view_message);
            name = itemView.findViewById(R.id.text_view_name);
        }

        public void bind(Message message) {
            if (message.getText().isEmpty()) {
                textViewMessage.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
            } else {
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
        notifyDataSetChanged();
        return true;
    }
}
