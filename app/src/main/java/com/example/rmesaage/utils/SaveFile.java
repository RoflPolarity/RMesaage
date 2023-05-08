package com.example.rmesaage.utils;

import com.example.rmesaage.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class SaveFile {
    public static boolean saveFile(File file, String s){
        AtomicBoolean res = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (file.exists()){
                    try {
                        FileWriter fw  = new FileWriter(file);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(s);
                        bw.flush();
                        bw.close();
                        res.set(true);
                    } catch (IOException e) {
                        res.set(false);
                    }
                }else {
                    try {
                        file.createNewFile();
                        saveFile(file,s);
                    } catch (IOException ignored) {
                    }
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
    public static User readFile(File file){
        final User[] user = new User[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (file.exists()){
                    try {
                        FileReader fr = new FileReader(file);
                        BufferedReader br = new BufferedReader(fr);
                        String line = br.readLine();
                        String[] s = line.split(",");
                        System.out.println(Arrays.toString(s));
                        user[0] = new User(s[0],s[1]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else user[0] = null;
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return user[0];
    }
}
