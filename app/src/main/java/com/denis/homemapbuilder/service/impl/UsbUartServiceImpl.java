package com.denis.homemapbuilder.service.impl;


import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.denis.homemapbuilder.service.MovementService;
import com.denis.homemapbuilder.service.UsbUartService;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.io.IOException;
import java.util.List;

public class UsbUartServiceImpl implements UsbUartService {

    private final String TAG = UsbUartServiceImpl.class.getSimpleName();

    private Activity activity;

    public UsbUartServiceImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void sendMessage(byte message) {
        try {
            // Find all available drivers from attached devices.
            UsbManager manager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                Log.w(TAG, "There is no available usb drivers. Message wasn't sent.");
                return;
            }

            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
                Log.w(TAG, "Couldn't open device. You probably haven't permissions. Message wasn't sent.");
                return;
            }

            // Read some data! Most have just one port (port 0).
            UsbSerialPort port = driver.getPorts().get(0);

            port.open(connection);
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            Log.i(TAG, "Connection is opened");
//            try {
//
//            if (message == 0) {
//                uartSendPackage('C', "STOP_ENG", port);
//            } else if (message == 1) {
//                uartSendPackage('C', "LEFT_ENG", port);
//            } else if (message == 2) {
//                uartSendPackage('C', "RIGH_ENG", port);
//            }

            int a = 0;
            while (a == 0) {
                byte buffer[] = new byte[15];
                Log.i(TAG, "Starting reading...");
                int bytesNumber = port.read(buffer, 10000);
                Log.i(TAG, "Reading is finished. Bytes number = " + bytesNumber + "]");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < buffer.length; i++) {
                    sb.append(buffer[i]);
                }
                Log.i(TAG, "Bytes: " + sb);
            }


//                byte buffer[] = new byte[1];
//                buffer[0] = message;
//                int numBytesWrite = port.write(buffer, 10000);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
            port.close();
            Log.i(TAG, "Connection is closed");
//            }

        } catch (IOException e) {
            Log.e(TAG, "Error. Message wasn't sent.", e);
        }

    }

    private void readUart(UsbSerialPort port) {
//        port.read();
    }

    private void uartSendPackage(char packageType, String message, UsbSerialPort port) {
        char controlSum = 0;
        uartSendChar('S', port);
        uartSendChar(packageType, port);
        for (int i = 0; i < 8; i++) {
            uartSendChar(message.charAt(i), port);
            controlSum ^= message.charAt(i);
        }

        uartSendChar(controlSum, port);
        uartSendChar('F', port);
        Log.i(TAG, "Message \"" + message + "\" was sent by usb. Package type ('C' = Command, 'D' = Data): " + packageType);
    }

    private void uartSendChar(char payload, UsbSerialPort port) {
        try {
            Thread.sleep(20);
            byte buffer[] = new byte[1];
            buffer[0] = (byte) payload;
            port.write(buffer, 10000);
            Log.d(TAG, "Char '" + payload + "' was sent by usb");
        } catch (IOException | InterruptedException e) {
            Log.i(TAG, "Error was occurred while sending char");
        }
    }

}
