package com.example.rmesaage.utils;

import android.os.AsyncTask;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class server_utils{
    private static final String SERVER_IP = "80.254.123.76";
    private static ObjectOutputStream out;
    private static ObjectInputStream OIS;
    private static Socket socket;


    public static void initializeStreams() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(SERVER_IP, 2511);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    OIS = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public static boolean auth(String username,String password){
        AtomicBoolean res = new AtomicBoolean();
                try{
                    System.out.println(out);
                    Response response = new Response("Auth",username,password,null,null,"user");
                    out.writeObject(response);
                    response = (Response<?>) OIS.readObject();
                    res.set((Boolean) response.getData());
                } catch (IOException | ClassNotFoundException e) {
                    res.set(false);
                    e.printStackTrace();
                }
        return res.get();
    }

    public static boolean sendMessage(Message messages){
            AtomicBoolean res = new AtomicBoolean();
            try {
                Response<?> response = new Response<>("SendMessage", messages.getMessageUser(), null, messages.getSendTo(), messages, "user");
                out.writeObject(response);
                response = (Response<?>) OIS.readObject();
                res.set((Boolean) response.getData());
                socket.close();
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
                    response = (Response<?>) OIS.readObject();
                    res.set((boolean) response.getData());
                    socket.close();
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
                    response = (Response<?>) OIS.readObject();
                    res.set((Boolean) response.getData());
                    socket.close();
                } catch (IOException | ClassNotFoundException e) {
                    res.set(false);
                }
        return res.get();
    }
}
