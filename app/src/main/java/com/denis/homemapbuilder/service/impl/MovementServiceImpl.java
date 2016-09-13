package com.denis.homemapbuilder.service.impl;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.denis.homemapbuilder.model.EngineType;
import com.denis.homemapbuilder.service.MovementService;
import com.denis.homemapbuilder.service.UsbUartService;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class MovementServiceImpl implements MovementService {

    private UsbManager usbManager;

    public MovementServiceImpl(Activity activity) {
        this.usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
    }


    private UsbUartService usbUartService;

    public MovementServiceImpl(UsbUartService usbUartService) {
        this.usbUartService = usbUartService;
    }


    @Override
    public void startEngine(int speed, EngineType engineType) {

    }

    @Override
    public void stopEngine(EngineType engineType) {

    }

    @Override
    public void moveForward(int speed) {

    }

    @Override
    public void moveBackward(int speed) {

    }

    @Override
    public void turnAroundLeft(int speed) {

    }

    @Override
    public void turnAroundRight(int speed) {

    }

    private void sendMessage(byte message) {
        try {
            // Find all available drivers from attached devices.
            UsbManager manager = usbManager;
            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                return;
            }

            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
                return;
            }

            // Read some data! Most have just one port (port 0).
            UsbSerialPort port = driver.getPorts().get(0);

            port.open(connection);
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//            try {

            if (message > 2) {
                uartSendPackage('C', "00000000", port);
            } else {
                uartSendPackage('C', "00000001", port);
            }
//                byte buffer[] = new byte[1];
//                buffer[0] = message;
//                int numBytesWrite = port.write(buffer, 10000);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
            port.close();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
    }

    private void uartSendChar(char payload, UsbSerialPort port) {
        try {
            byte buffer[] = new byte[1];
            buffer[0] = (byte) payload;
            port.write(buffer, 10000);
            Thread.sleep(20);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO: временный метод, пока нет нормального управления
    public void publicSendMessage(byte message) {
        usbUartService.sendMessage(message);
//        sendMessage(message);
    }

}

