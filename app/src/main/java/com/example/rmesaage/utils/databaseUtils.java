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
import com.example.rmesaage.Chat.UserChat;
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

    static UserChat Userchat;

    public static void setChat(UserChat chat){
        Userchat = chat;
    }

    public static void createTableIfNotExists(Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
        database.beginTransaction();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, author TEXT, sendTo TEXT, text TEXT)";
        database.execSQL(createTableQuery);
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }


    public static ArrayList<ChatLstItem> getChats(String author, Context context) {
        ArrayList<ChatLstItem> chats = new ArrayList<>();
            Set<String> sendTo = new HashSet<>();
            String selection = "author=?";
            String[] selectionArgs = new String[]{author};
            SQLiteDatabase database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
            database.beginTransaction();
            Cursor cursor = database.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

            int columnIndexSendTo = cursor.getColumnIndex("sendTo");
            while (cursor.moveToNext()) {
                String sendToValue = cursor.getString(columnIndexSendTo);
                if (sendToValue != null) {
                    sendTo.add(sendToValue);
                }
            }
            cursor.close();
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            for (String recipient : sendTo) {
                ArrayList<Message> msList = getMsList(author, recipient,context);
                if (!msList.isEmpty()) {
                    Message lastMessage = msList.get(msList.size() - 1);
                    chats.add(new ChatLstItem(lastMessage.getText(), recipient));
                }
            }

            return chats;
    }

    public static ArrayList<Message> getMsList(String author, String sendTo,Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
        ArrayList<Message> msList = new ArrayList<>();
        String selection = "(author=? AND sendTo=?) OR (author=? AND sendTo=?)";
        String[] selectionArgs = new String[]{author, sendTo, sendTo, author};
        database.beginTransaction();
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
                        if (text!=null){
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
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
        return msList;
    }
    public static void insert(Message message, Context context) {
        if (message.getText().contains("NewMessage---")){
            message.setText(message.getText().replace("NewMessage---",""));
            SQLiteDatabase database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("author", message.getMessageUser());
            values.put("sendTo", message.getSendTo());
            values.put("text", message.getText());
            database.beginTransaction();
            database.insert(TABLE_NAME, null, values);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            Userchat.OnNewMessageRec(message);
        }else {
            SQLiteDatabase database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("author", message.getMessageUser());
            values.put("sendTo", message.getSendTo());
            values.put("text", message.getText());
            database.beginTransaction();
            database.insert(TABLE_NAME, null, values);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
        }


    }
    public static void insert(Message message,String path, Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
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
        database.close();
    }
}
