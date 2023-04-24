package com.example.rmesaage.utils;

import com.example.rmesaage.Chat.Message;
import com.example.rmesaage.interfaces.MessageListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MessageReceiverManager {

    private static final int SERVER_PORT = 3696;
    private static MessageReceiverManager instance;
    private final List<MessageListener> listenerList = new ArrayList<>();
    private MessageReceiverThread messageReceiverThread;

    public MessageReceiverThread getMessageReceiverThread() {
        return messageReceiverThread;
    }

    private MessageReceiverManager() {
        // Приватный конструктор для Singleton
    }

    public static MessageReceiverManager getInstance() {
        if (instance == null) {
            instance = new MessageReceiverManager();
        }
        return instance;
    }

    public void addMessageListener(MessageListener listener) {
        listenerList.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        listenerList.remove(listener);
    }

    public void start() {
        if (messageReceiverThread == null || !messageReceiverThread.isAlive()) {
            messageReceiverThread = new MessageReceiverThread(SERVER_PORT);
            messageReceiverThread.start();
        }
        System.out.println(messageReceiverThread.isAlive());
    }

    public void stop() {
        if (messageReceiverThread != null) {
            messageReceiverThread.interrupt();
        }
    }

    private class MessageReceiverThread extends Thread {

        private final int serverPort;

        MessageReceiverThread(int serverPort) {
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                while (true) {
                    System.out.println("Server is Active");
                    try (Socket socket = serverSocket.accept()) {
                        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                        Message message = (Message) inputStream.readObject();
                        for (MessageListener listener : listenerList) {
                            listener.onMessageReceived(message);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
