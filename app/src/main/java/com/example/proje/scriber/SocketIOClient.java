package com.example.proje.scriber;

import android.app.Activity;

import java.net.Socket;
import java.net.URISyntaxException;

import io.socket.client.IO;

/**
 * Created by sahin on 30.04.2016.
 */
public class SocketIOClient {

    private static io.socket.client.Socket mSocket;

    private static void initSocket(Activity activity,String url) {
        try {
            mSocket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static io.socket.client.Socket getInstance(Activity activity) {
        if (mSocket != null) {
            return mSocket;
        } else {
            //initSocket(activity,"http://192.168.1.104:8081"); // -> Ev ip
            initSocket(activity,"http://192.168.137.1:8081"); // -> Lokal ip
            return mSocket;
        }
    } }