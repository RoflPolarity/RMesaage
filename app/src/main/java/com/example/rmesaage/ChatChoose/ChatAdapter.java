package com.example.rmesaage.ChatChoose;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmesaage.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public List<ChatLstItem> chatList;
    private OnItemClickListener listener;

    public List<ChatLstItem> getChatList() {
        return chatList;
    }

    public ChatAdapter(List<ChatLstItem> lst) {
        this.chatList = lst;
    }

    public ChatAdapter(){}

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatLstItem chat = chatList.get(position);
        holder.bind(chat);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getLayoutPosition();
                if (listener != null) {
                    listener.onItemClick(clickedPosition);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvLastMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_name);
            tvLastMessage = itemView.findViewById(R.id.textView_last_message);
        }

        public void bind(ChatLstItem messages) {
            tvName.setText(messages.getName());
            tvLastMessage.setText(messages.getLastMessage());
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void bind(String username,String lasMessage){
        chatList.clear();
        chatList.add(new ChatLstItem(lasMessage, username));
        notifyDataSetChanged();
    }
}
