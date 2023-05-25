package com.example.rmesaage.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.ChatChoose.ChatLstItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class databaseUtils{
    private static final String TABLE_NAME = "messages";
    private SQLiteDatabase database;




    public databaseUtils(Context context) {
        database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
        System.out.println("connected to database");
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, author TEXT, sendTo TEXT, text TEXT)";
        database.execSQL(createTableQuery);
    }


    public ArrayList<ChatLstItem> getChats(String author) {
        ArrayList<ChatLstItem> chats = new ArrayList<>();
        Set<String> sendTo = new HashSet<>();
        String selection = "author=?";
        String[] selectionArgs = new String[]{author};

        Cursor cursor = database.query(TABLE_NAME,null,selection,selectionArgs,null,null,null);
        int columnIndexSendTo = cursor.getColumnIndex("sendTo");
        while (cursor.moveToNext()) {
            String sendToValue = cursor.getString(columnIndexSendTo);
            if (sendToValue != null) {
                sendTo.add(sendToValue);
            }
        }
        cursor.close();
        for (String recipient : sendTo) {
            ArrayList<Message> msList = getMsList(author, recipient);
            if (!msList.isEmpty()) {
                Message lastMessage = msList.get(msList.size() - 1);
                chats.add(new ChatLstItem(lastMessage.getText(), recipient));
            }
        }
        return chats;
    }

    public ArrayList<Message> getMsList(String author, String sendTo) {
        ArrayList<Message> msList = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String selection = "author=? AND sendTo=?";
                String[] selectionArgs = new String[]{author, sendTo};

                Cursor cursor = database.query("messages", null, selection, selectionArgs, null, null, null);
                int columnIndexId = cursor.getColumnIndex("id");
                int columnIndexAuthor = cursor.getColumnIndex("author");
                int columnIndexSendTo = cursor.getColumnIndex("sendTo");
                int columnIndexText = cursor.getColumnIndex("text");

                if (columnIndexId == -1 || columnIndexAuthor == -1 || columnIndexSendTo == -1 || columnIndexText == -1) {
                    cursor.close();
                }

                while (cursor.moveToNext()) {
                    try {
                        int id = cursor.getInt(columnIndexId);
                        String sender = cursor.getString(columnIndexAuthor);
                        String recipient = cursor.getString(columnIndexSendTo);
                        String text = cursor.getString(columnIndexText);
                        msList.add(new Message(id, sender, text, null, recipient));
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();

            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return msList;
    }
    public void insert(Message message) {
        ContentValues values = new ContentValues();
        values.put("author", message.getMessageUser());
        values.put("sendTo", message.getSendTo());
        values.put("text", message.getText());
        database.insert(TABLE_NAME, null, values);
    }
}
