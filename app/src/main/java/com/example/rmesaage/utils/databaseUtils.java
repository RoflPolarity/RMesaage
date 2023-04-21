package com.example.rmesaage.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rmesaage.ChatChoose.ChatLstItem;

import java.util.ArrayList;

public class databaseUtils extends SQLiteOpenHelper {
    private static final String DB_NAME = "message.db";
    private static final  int DB_VERSION = 1;


    public static class message{
        private int id;
        private String author;
        private String sendTo;
        private String text;


        public message(int id, String author, String sendTo, String text) {
            this.id = id;
            this.author = author;
            this.sendTo = sendTo;
            this.text = text;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getSendTo() {
            return sendTo;
        }

        public void setSendTo(String sendTo) {
            this.sendTo = sendTo;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }


    public databaseUtils(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF  NOT EXISTS messages(id INTEGER PRIMARY KEY AUTOINCREMENT, author TEXT, sendTo TEXT, text TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<ChatLstItem> getChats(String author) {
        ArrayList<ChatLstItem> chats = new ArrayList<>();
        ArrayList<String> sendTo = new ArrayList<>();
        String selection = "author=?";
        String[] selectionArgs = new String[]{author};
        Cursor cursor = getWritableDatabase().query("messages",null,selection,selectionArgs,null,null,null);
        while (cursor.moveToNext()){
            sendTo.add(cursor.getString(cursor.getColumnIndex("sendTo")));
        }
        for (int i = 0; i < sendTo.size(); i++) {
            ArrayList<message> msLst = getMsList(author,sendTo.get(i));
            chats.add(new ChatLstItem(msLst.get(msLst.size()-1).text,sendTo.get(i)));
        }
        return chats;
    }

    public ArrayList<message> getMsList(String author, String sendTo) {
        ArrayList<message> msList = new ArrayList<>();
        String selection = "author=? AND sendTo=?";
        String[] selectionArgs = new String[]{author,sendTo};
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("messages",null,selection,selectionArgs,null,null,null);
        while (cursor.moveToNext()){
            try {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String sender = cursor.getString(cursor.getColumnIndex("author"));
                String SendTo = cursor.getString(cursor.getColumnIndex("sendTo"));
                String text = cursor.getString(cursor.getColumnIndex("text"));
                msList.add(new message(id, sender, SendTo, text));
            }catch (SQLiteException e){
                e.printStackTrace();
            }

        }
        cursor.close();
        return msList;
    }

    public void insert(message message){
        ContentValues values = new ContentValues();
        values.put("author",message.getAuthor());
        values.put("sendTo",message.getSendTo());
        values.put("text",message.getText());
        getWritableDatabase().insert("messages",null,values);
    }
}
