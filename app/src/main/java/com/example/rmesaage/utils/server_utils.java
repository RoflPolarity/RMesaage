package com.example.rmesaage.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.Chat.UserChat;
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

public class server_utils {
    private static final String SERVER_IP = "80.254.123.76";
    private static ObjectOutputStream out;
    private static ObjectInputStream OIS;
    private static Socket socket;
    static Thread thread;
    static ArrayList<Message> synced = new ArrayList<>();
    static int searched = -1;
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
    public static boolean auth(String username, String password) {
        AtomicBoolean res = new AtomicBoolean();
        try {
            Response response = new Response("Auth", username, password, null, null, "user");
            out.writeObject(response);
            out.flush();
            response = (Response<?>) OIS.readObject();
            res.set((Boolean) response.getData());
        } catch (IOException | ClassNotFoundException e) {
            res.set(false);
        }
        return res.get();
    }
    public static boolean sendMessage(Message messages, Context context) {
        AtomicBoolean res = new AtomicBoolean();
        Response<?> response = null;
        try {
            if (messages.getBitMaps() != null) {
                if (messages.getBitMaps().size() > 0) {
                    response = new Response<>("SendMessage", messages.getMessageUser(), null, messages.getSendTo(), messages.getBitMaps(), "user");
                }
            } else
                response = new Response<>("SendMessage", messages.getMessageUser(), null, messages.getSendTo(), messages.getText(), "user");
            out.writeObject(response);
            out.flush();
            databaseUtils.insert(messages, context);
        } catch (Exception e) {
            e.printStackTrace();
            res.set(false);
        }
        return res.get();
    }
    public static void getNewMessageThread(Context context) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        // Проверяем флаг isSyncing для определения, нужно ли читать объекты
                        if (OIS != null) {
                            System.out.println(true);
                            Object object = OIS.readObject();
                            if (object instanceof Response) {
                                Response<?> response = (Response<?>) object;
                                System.out.println(response.getComma());
                                System.out.println();
                                if ("NewMessage".equals(response.getComma())) {
                                    String message = "";
                                    if (response.getData() instanceof String){
                                        message = (String) response.getData();
                                    databaseUtils.insert(new Message(0, response.getUsername(), "NewMessage---" + message, null, response.getSendTo()), context);
                                    } else if (response.getData() instanceof ArrayList) {
                                        ArrayList<byte[]> images = (ArrayList<byte[]>) response.getData();
                                        StringBuilder sb = new StringBuilder();
                                        for (int i = 0; i < images.size(); i++) {
                                            byte[] imageBytes = images.get(i);
                                            String filePath = "image" + i + ".jpg";
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
                                        databaseUtils.insert(new Message(0, response.getUsername(), sb.toString(), null, response.getSendTo()), context);
                                    }

                                }else if ("Sync".equals(response.getComma())) {
                                    synced = (ArrayList<Message>) response.getData();
                                }else if ("Search".equals(response.getComma())){
                                    if ((Boolean) response.getData()){
                                        searched = 1;
                                    }else  searched = 0;
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

    public static boolean reg(String username, String password) {

        AtomicBoolean res = new AtomicBoolean();
        try {
            Response<?> response = new Response<>("Register", username, password, null, null, "user");
            out.writeObject(response);
            out.flush();
            response = (Response<?>) OIS.readObject();
            res.set((boolean) response.getData());
        } catch (IOException | ClassNotFoundException e) {
            res.set(false);
        }
        return res.get();
    }
    public static boolean searchByUsername(String username) {
        try {
            Response<?> response = new Response<>("Search", username, null, null, null, "user");
            out.writeObject(response);
            out.flush();
            while (searched==-1){}
            if (searched == 1) return true;
            else if (searched==0) return false;
        } catch (IOException e) {
            return false;
        }
        return false;
    }
    public static ArrayList<Message> sync(String username) {
        thread.interrupt();
        try {
            Response<?> response = new Response<>("Sync", username, null, null, null, "user");
            out.writeObject(response);
            out.flush();

            while (synced.size()==0){

            }
            return synced;
        } catch (Exception e) {
            e.printStackTrace();
        }
        thread.start();
        return null;
    }
}
