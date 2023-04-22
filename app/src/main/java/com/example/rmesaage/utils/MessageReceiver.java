package com.example.rmesaage.utils;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.interfaces.MessageListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MessageReceiver implements Runnable {
    private boolean isRunning = true;
    ServerSocket serverSocket;
    private final ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void stop() {
        isRunning = false;
    }

    //TODO: Разобраться, что делать с портом.
    //Надо найти свободный
    @Override
    public void run() {
        while (isRunning) {
            try {
                serverSocket = new ServerSocket(0);
                System.out.println(serverSocket.getLocalPort());
                Socket socket = serverSocket.accept();
                ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) OIS.readObject();
                messageListeners.forEach(x->x.onMessageReceived(message));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
