package com.denis.homemapbuilder.service.impl;


import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.denis.homemapbuilder.model.UartCommand;
import com.denis.homemapbuilder.service.UsbUartService;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

public class UsbUartServiceImpl implements UsbUartService {

    private static final char MESSAGE_DELIMITER = '`';
    private static final char ARGUMENTS_DELIMITER = '|';

    private final String TAG = UsbUartServiceImpl.class.getSimpleName();

    private final Activity activity;

    public UsbUartServiceImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void sendCommand(UartCommand command, List<String> args) {
        StringBuilder msgBuilder = new StringBuilder(String.valueOf(command.getCode()));
        for (String arg : args) {
            msgBuilder.append(arg).append(ARGUMENTS_DELIMITER);
        }
        msgBuilder.deleteCharAt(msgBuilder.length() - 1);
        msgBuilder.append(MESSAGE_DELIMITER);
        sendMessage(msgBuilder.toString());
    }

    @Override
    public void sendMessage(String message) {
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
            UsbSerialPort port = null;
            try {
                port = driver.getPorts().get(0);

                port.open(connection);
                port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                Log.i(TAG, "Connection is opened");
                uartSendString(message, port);
//                ((MainActivity) activity).setTextViewText(readUart(port));
            } finally {
                if (port != null) {
                    port.close();
                }
                Log.i(TAG, "Connection is closed");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error. Message wasn't sent.", e);
        }

    }

    private String readUart(UsbSerialPort port) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            byte[] buffer = new byte[20];
            port.read(buffer, 1000);
            String readString = new String(buffer);
            sb.append(readString);
            if (readString.contains("$")) {
                return sb.toString();
            }
        }
    }

    private void uartSendString(String payload, UsbSerialPort port) {
        for (int i = 0; i < payload.length(); i++) {
            uartSendChar(payload.charAt(i), port);
        }
        Log.i(TAG, "Message \"" + payload + "\" was sent by usb");
    }

    private synchronized void uartSendChar(char payload, UsbSerialPort port) {
        try {
            Thread.sleep(20);
            byte buffer[] = new byte[1];
            buffer[0] = (byte) payload;
            port.write(buffer, 10000);
//            Log.d(TAG, "Char '" + payload + "' was sent by usb");
        } catch (IOException | InterruptedException e) {
            Log.i(TAG, "Error was occurred while sending char", e);
        }
    }

}
