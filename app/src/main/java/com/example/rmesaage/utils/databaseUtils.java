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

public class databaseUtils{
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "messages";
    private SQLiteDatabase database;




    public databaseUtils(Context context) {
        database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
        System.out.println("connected to database");
        createTableIfNotExists(TABLE_NAME);
    }

    public void createTableIfNotExists(String ChatName) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + ChatName +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, author TEXT, sendTo TEXT, text TEXT)";
        database.execSQL(createTableQuery);
    }


    public ArrayList<ChatLstItem> getChats(String author) {
        ArrayList<ChatLstItem> chats = new ArrayList<>();
        ArrayList<String> sendTo = new ArrayList<>();
        String selection = "author=?";
        String[] selectionArgs = new String[]{author};

        Cursor cursor = database.query(TABLE_NAME,null,selection,selectionArgs,null,null,null);
        int columnIndexSendTo = cursor.getColumnIndex("sendTo");
        if (columnIndexSendTo == -1) {
            // Handle the case when the column is not found
            cursor.close();
            return chats;
        }

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
                System.out.println("true");

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
    public void insert(Message message, String tableName) {
        ContentValues values = new ContentValues();
        values.put("author", message.getMessageUser());
        values.put("sendTo", message.getSendTo());
        values.put("text", message.getText());
        database.insert(tableName, null, values);
    }
}
