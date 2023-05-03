package com.example.rmesaage.utils;

import com.example.rmesaage.Response;
import com.example.rmesaage.User;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
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
                    e.printStackTrace();
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
                    e.printStackTrace();
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
}
