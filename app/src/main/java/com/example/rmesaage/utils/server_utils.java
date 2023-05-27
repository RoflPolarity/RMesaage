package com.example.rmesaage.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Response;

import java.io.File;
import java.io.FileOutputStream;
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
    private static boolean isSyncing;
    public static ObjectInputStream getOIS() {
        return OIS;
    }

    public static void initializeStreams() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(SERVER_IP, 2511);
                    InputStream inputStream = socket.getInputStream();
                    OIS = new ObjectInputStream(inputStream);
                    OutputStream outputStream = socket.getOutputStream();
                    out = new ObjectOutputStream(outputStream);
                    server_utils.socket = socket;
                    server_utils.isSyncing = false;
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
            Response<?> response;
            try {
                if (messages.getBitMaps().size()>0){
                    response = new Response<>("SendMessage", messages.getMessageUser(), null, messages.getSendTo(), messages.getBitMaps(), "user");
                }else response = new Response<>("SendMessage", messages.getMessageUser(), null, messages.getSendTo(), messages.getText(), "user");
                out.writeObject(response);
                out.flush();
                databaseUtils.insert(messages);
            } catch (Exception e) {
                e.printStackTrace();
                res.set(false);
            }
            return res.get();
    }
    public static void getNewMessageThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        // Проверяем флаг isSyncing для определения, нужно ли читать объекты
                        if (isSyncing) {
                            Object object = OIS.readObject();
                            if (object instanceof Response) {
                                Response<?> response = (Response<?>) object;
                                if ("NewMessage".equals(response.getComma())) {
                                    String message = "";
                                    try{
                                        message = (String) response.getData();
                                        databaseUtils.insert(new Message(0, response.getUsername(), message, null, response.getSendTo()));
                                    }catch (Exception e){
                                        ArrayList<byte[]> images = (ArrayList<byte[]>) response.getData();
                                        StringBuilder sb = new StringBuilder();
                                        for (int i = 0; i < images.size(); i++) {
                                            byte[] imageBytes = images.get(i);
                                            String filePath = "image"+i+".jpg";
                                            try {
                                                File file = new File(filePath);
                                                FileOutputStream fos = new FileOutputStream(file);
                                                fos.write(imageBytes);
                                                fos.close();
                                                System.out.println("Файл " + filePath + " успешно создан.");
                                                sb.append(file.getAbsolutePath()).append("   ");
                                            } catch (IOException d) {
                                                d.printStackTrace();
                                                System.out.println("Ошибка при создании файла " + filePath);
                                            }
                                        }
                                        databaseUtils.insert(new Message(0, response.getUsername(), sb.toString(), null, response.getSendTo()));
                                    }


                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName("NewMessages");
        thread.start();
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
    public static ArrayList<Message> sync(String username) {
        try {
            isSyncing = false; // Устанавливаем флаг, чтобы поток не читал объекты

            Response<?> response = new Response<>("Sync", username, null, null, null, "user");
            out.writeObject(response);
            out.flush();

            Object object = OIS.readObject();
            if (object instanceof Response<?>) {
                response = (Response<?>) object;
                ArrayList<Message> res = (ArrayList<Message>) response.getData();
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isSyncing = true; // Снимаем приостановку чтения объектов
        }
        return null;
    }

}
