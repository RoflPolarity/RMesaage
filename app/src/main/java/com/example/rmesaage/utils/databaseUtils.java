package com.example.rmesaage.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.ChatChoose.ChatLstItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class databaseUtils{
    private static final String TABLE_NAME = "messages";
    private static SQLiteDatabase database;



    public static void init(Context context){
                database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
                createTableIfNotExists();
    }

    private static void createTableIfNotExists() {
        database.beginTransaction();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, author TEXT, sendTo TEXT, text TEXT)";
        database.execSQL(createTableQuery);
        database.setTransactionSuccessful();
        database.endTransaction();
    }


    public static ArrayList<ChatLstItem> getChats(String author) {
        ArrayList<ChatLstItem> chats = new ArrayList<>();
            Set<String> sendTo = new HashSet<>();
            String selection = "author=?";
            String[] selectionArgs = new String[]{author};
        System.out.println(database);
            database.beginTransaction();
            Cursor cursor = database.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
            int columnIndexSendTo = cursor.getColumnIndex("sendTo");
            while (cursor.moveToNext()) {
                String sendToValue = cursor.getString(columnIndexSendTo);
                if (sendToValue != null) {
                    sendTo.add(sendToValue);
                }
            }

            for (String recipient : sendTo) {
                ArrayList<Message> msList = getMsList(author, recipient);
                if (!msList.isEmpty()) {
                    Message lastMessage = msList.get(msList.size() - 1);
                    chats.add(new ChatLstItem(lastMessage.getText(), recipient));
                }
            }
            database.setTransactionSuccessful();
            database.endTransaction();
            return chats;
    }

    public static ArrayList<Message> getMsList(String author, String sendTo) {
        database.beginTransaction();
        ArrayList<Message> msList = new ArrayList<>();
                String selection = "(author=? AND sendTo=?) OR (author=? AND sendTo=?)";
                String[] selectionArgs = new String[]{author, sendTo, sendTo, author};
                Cursor cursor = database.query("messages", null, selection, selectionArgs, null, null, null);
                int columnIndexId = cursor.getColumnIndex("id");
                int columnIndexAuthor = cursor.getColumnIndex("author");
                int columnIndexSendTo = cursor.getColumnIndex("sendTo");
                int columnIndexText = cursor.getColumnIndex("text");

                while (cursor.moveToNext()) {
                    try {
                        int id = cursor.getInt(columnIndexId);
                        String sender = cursor.getString(columnIndexAuthor);
                        String recipient = cursor.getString(columnIndexSendTo);
                        String text = cursor.getString(columnIndexText);
                        if (text.contains("Image---")){
                            text = text.replace("Image---","");
                            ArrayList<byte[]> bitMaps = new ArrayList<>();
                            String[] splittedText = text.split("   ");
                            for (int i = 0; i < splittedText.length; i++) {
                                File file = new File(splittedText[i]);
                                try {
                                    FileInputStream fis = new FileInputStream(file);
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                                    byte[] buffer = new byte[1024];
                                    int bytesRead;
                                    while ((bytesRead = fis.read(buffer)) != -1) {
                                        bos.write(buffer, 0, bytesRead);
                                    }
                                    byte[] fileBytes = bos.toByteArray();
                                    bitMaps.add(fileBytes);
                                    fis.close();
                                    bos.close();
                                } catch (IOException n) {
                                    n.printStackTrace();
                                }
                            }
                            msList.add(new Message(id, sender, null, bitMaps, recipient));
                        }else msList.add(new Message(id, sender, text, null, recipient));
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
        database.setTransactionSuccessful();
        database.endTransaction();
        return msList;
    }
    public static void insert(Message message) {
        if (message.getText().equals("")) return;
        ContentValues values = new ContentValues();
        values.put("author", message.getMessageUser());
        values.put("sendTo", message.getSendTo());
        values.put("text", message.getText());
        database.beginTransaction();
        database.insert(TABLE_NAME, null, values);
        database.setTransactionSuccessful();
        database.endTransaction();
    }
    public static void insert(Message message,String path) {
        System.out.println("Вставка");
        if (message.getBitMaps().size()>0 && !path.equals("")){
            ContentValues values = new ContentValues();
            values.put("author", message.getMessageUser());
            values.put("sendTo", message.getSendTo());
            values.put("text", "Image---"+path);
            database.beginTransaction();
            database.insert(TABLE_NAME, null, values);
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }
}
