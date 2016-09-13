package com.denis.homemapbuilder.service.impl;


import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.denis.homemapbuilder.service.MovementService;
import com.denis.homemapbuilder.service.WebSocketService;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import javax.inject.Inject;

public class WebSocketServiceImpl implements WebSocketService {

    private final String CLASS_TAG = WebSocketServiceImpl.class.getName();

    private MovementService movementService;

    public WebSocketServiceImpl(MovementService movementService) {
        this.movementService = movementService;
        initWebSocket();
    }

    private void initWebSocket() {
        final String url = "ws://192.168.1.154:8080/websocket";
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
                        byte b = (byte) Integer.parseInt(s);
                        if (movementService instanceof MovementServiceImpl) {
                            ((MovementServiceImpl) movementService).publicSendMessage(b);
                        } else {
                            throw new RuntimeException("movementService неверного типа");
                        }
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
