package com.example.rmesaage.utils;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class server_utils {

    private static final String SERVER_IP = "80.254.123.76";
    public static boolean auth(String username,String password){
        AtomicBoolean res = new AtomicBoolean();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(SERVER_IP,2511);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("Auth,"+username+","+password);
                    ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                    Response<?> response = (Response<?>) OIS.readObject();
                    res.set((Boolean) response.getData());
                    socket.close();
                } catch (IOException | ClassNotFoundException e) {
                    res.set(false);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res.get();
    }

    public static boolean sendMessage(String username,String sendTo,ArrayList<byte[]> text){
        AtomicBoolean res = new AtomicBoolean();
        StringBuilder builder = new StringBuilder();
        if (text.size()==1) builder.append(Arrays.toString(text.get(0)).replace("[","").replace("]",""));
        else {
            for (int i = 0; i < text.size(); i++) {
                builder.append(Arrays.toString(text.get(i)).replace("[","").replace("]","")).append(" --- ");
            }
        }
        System.out.println(builder);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(SERVER_IP, 2511);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("SendMessage,"+username+","+sendTo+","+builder);
                    ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                    Response<?> response = (Response<?>) OIS.readObject();
                    res.set((Boolean) response.getData());
                    socket.close();
                }catch (Exception e){
                    res.set(false);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res.get();
    }

    public static boolean sendMessage(String username,String sendTo,String text){
        AtomicBoolean res = new AtomicBoolean();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(SERVER_IP, 2511);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("SendMessage,"+username+","+sendTo+","+text);
                    ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                    Response<?> response = (Response<?>) OIS.readObject();
                    res.set((Boolean) response.getData());
                    socket.close();
                }catch (Exception e){
                    res.set(false);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res.get();
    }

    public static boolean reg(String username,String password){

        AtomicBoolean res = new AtomicBoolean();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(SERVER_IP,2511);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("Register,"+username+","+password);
                    ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                    Response<?> response = (Response<?>) OIS.readObject();
                    res.set((boolean) response.getData());
                    socket.close();
                } catch (IOException | ClassNotFoundException e) {
                    res.set(false);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res.get();
    }
    public static boolean searchByUsername(String username){
        AtomicBoolean res = new AtomicBoolean();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(SERVER_IP,2511);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("Search,"+username);
                    ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                    Response<?> response = (Response<?>) OIS.readObject();
                    res.set((Boolean) response.getData());
                    socket.close();
                } catch (IOException | ClassNotFoundException e) {
                    res.set(false);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res.get();
    }
    public static ArrayList<Message> getMessage(String username, String sendTo){
        final ArrayList<Message>[] res = new ArrayList[]{new ArrayList<>()};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(SERVER_IP,2511);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("getMessages,"+username+","+sendTo);
                    ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                    Response<?> response = (Response<?>) OIS.readObject();
                    res[0] = (ArrayList<Message>) response.getData();
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res[0];
    }
    public static ArrayList<Message> getChats(String username){
        final ArrayList<Message>[] res = new ArrayList[]{new ArrayList<>()};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(SERVER_IP, 2511);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("GetChatLst," + username);
                    ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                    Response<?> response = (Response<?>) OIS.readObject();
                    ArrayList<Message> ex = (ArrayList<Message>) response.getData();
                    res[0] = ex;
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res[0];
    }
}
