package com.denis.homemapbuilder.service.impl;


import android.util.Log;

import com.denis.homemapbuilder.model.CommandMessage;
import com.denis.homemapbuilder.service.MovementService;
import com.denis.homemapbuilder.service.WebSocketService;
import com.google.gson.Gson;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

public class WebSocketServiceImpl implements WebSocketService {

    private final String CLASS_TAG = WebSocketServiceImpl.class.getName();

    private final MovementService movementService;

    public WebSocketServiceImpl(MovementService movementService) {
        this.movementService = movementService;
        initWebSocket();
    }

    private void initWebSocket() {
        final String url = "ws://192.168.0.11:8080/websocket";
        AsyncHttpClient.getDefaultInstance().websocket(url, "ws", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    Log.e(CLASS_TAG, "Web socket (" + url + " )connection error: ", ex);
                    return;
                }
                Log.i(CLASS_TAG, "Web socket connection established: " + url);
//                webSocket.send("4");
//                webSocket.send(new byte[10]);
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        Gson gson = new Gson();
                        CommandMessage commandMessage = gson.fromJson(s, CommandMessage.class);
                        movementService.sendMessage(commandMessage);
                    }
                });
                webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        System.out.println("I got some bytes!");
                        // note that this data has been read
                        byteBufferList.recycle();
                    }
                });
            }
        });
    }
}
