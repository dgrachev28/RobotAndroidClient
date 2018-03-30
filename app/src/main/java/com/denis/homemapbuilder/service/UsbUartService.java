package com.denis.homemapbuilder.service;


import com.denis.homemapbuilder.model.UartCommand;

import java.util.List;

public interface UsbUartService {

    void sendCommand(UartCommand command, List<String> args);

    void sendMessage(String message);

}
