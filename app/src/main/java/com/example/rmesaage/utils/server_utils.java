package com.example.rmesaage.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class server_utils{
    private static final String SERVER_IP = "80.254.123.76";
    private static ObjectOutputStream out;
    private static ObjectInputStream OIS;
    private static Socket socket;

    public static void initializeStreams() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(SERVER_IP, 2511);
                    OutputStream outputStream = socket.getOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(outputStream);

                    InputStream inputStream = socket.getInputStream();
                    ObjectInputStream in = new ObjectInputStream(inputStream);

                    server_utils.socket = socket;
                    server_utils.out = out;
                    server_utils.OIS = in;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean auth(String username,String password){
        AtomicBoolean res = new AtomicBoolean();
                try{
                    Response response = new Response("Auth",username,password,null,null,"user");
                    out.writeObject(response);
                    out.flush();
                    response = (Response<?>) OIS.readObject();
                    res.set((Boolean) response.getData());
                } catch (IOException | ClassNotFoundException e) {
                    res.set(false);
                }
        return res.get();
    }
    public static boolean sendMessage(Message messages, Context context){
            AtomicBoolean res = new AtomicBoolean();
            try {
                Response<?> response = new Response<>("SendMessage", messages.getMessageUser(), null, messages.getSendTo(), messages, "user");
                out.writeObject(response);
                out.flush();
                response = (Response<?>) OIS.readObject();
                if (((ArrayList<Message>) response.getData()).size()>0){
                   ArrayList<Message> lst = ((ArrayList<Message>) response.getData());
                    res.set(true);
                   databaseUtils utils = new databaseUtils(context);
                    for (int i = 0; i < lst.size(); i++) {
                        utils.insert(lst.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.set(false);
            }
            return res.get();
    }
    public static boolean reg(String username,String password){

        AtomicBoolean res = new AtomicBoolean();
                try{
                    Response<?> response = new Response<>("Register",username,password,null,null,"user");
                    out.writeObject(response);
                    out.flush();
                    response = (Response<?>) OIS.readObject();
                    res.set((boolean) response.getData());
                } catch (IOException | ClassNotFoundException e) {
                    res.set(false);
                }
        return res.get();
    }
    public static boolean searchByUsername(String username){
        AtomicBoolean res = new AtomicBoolean();
                try{
                    Response<?> response = new Response<>("Search",username,null,null,null,"user");
                    out.writeObject(response);
                    out.flush();
                    response = (Response<?>) OIS.readObject();
                    res.set((Boolean) response.getData());
                } catch (IOException | ClassNotFoundException e) {
                    res.set(false);
                }
        return res.get();
    }
    public static ArrayList<Message> sync(String username){
        try{
            Response<?> response = new Response<>("Sync",username,null,null,null,"user");
            out.writeObject(response);
            out.flush();
            response = (Response<?>) OIS.readObject();
            ArrayList<Message> res = (ArrayList<Message>) response.getData();
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
