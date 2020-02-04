package com.example.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

public class Network {
    public ObjectOutputStream out;
    public ObjectInputStream in;
    public Socket sock;
    private String serverIP;
    private int port;

    private String data;
    private String return_message = null;

    public Network(String serverIP, int port) {
            this.serverIP = serverIP;
            this.port = port;
    }

    public void connect() {
        Thread t = new Thread("Main Net Thread") {
            public void run() {
                try {
                    sock = new Socket(InetAddress.getByName(serverIP), port);
                    out = new ObjectOutputStream(sock.getOutputStream());
                    out.flush();
                    in = new ObjectInputStream(sock.getInputStream());
                    String key = in.readObject().toString();
                    MainActivity.encrypt = new aesEncrypt(key);
                    System.err.println(key);
                    System.err.println("Setup Encryption");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }


    public synchronized void sendMessage(final String message) {
        final String _message = message;
        Thread t = new Thread("Send Thread") {
            public void run() {
                try {
                    System.out.println(_message);
                    String toPrint = MainActivity.encrypt.encrypt(_message);
                    System.out.println(toPrint);
                    out.writeObject(toPrint);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
