package com.denis.homemapbuilder.service.impl;

import com.denis.homemapbuilder.component.CommandMessage2UartCommandConverter;
import com.denis.homemapbuilder.model.CommandMessage;
import com.denis.homemapbuilder.model.UartCommand;
import com.denis.homemapbuilder.service.MovementService;
import com.denis.homemapbuilder.service.UsbUartService;

import java.util.List;

public class MovementServiceImpl implements MovementService {

    private final UsbUartService usbUartService;
    private final CommandMessage2UartCommandConverter commandMessage2UartCommandConverter;


    public MovementServiceImpl(UsbUartService usbUartService, CommandMessage2UartCommandConverter commandMessage2UartCommandConverter) {
        this.usbUartService = usbUartService;
        this.commandMessage2UartCommandConverter = commandMessage2UartCommandConverter;
    }

    @Override
    public void sendMessage(CommandMessage commandMessage) {
        UartCommand uartCommand = commandMessage2UartCommandConverter.convert(commandMessage);
        List<String> args = commandMessage2UartCommandConverter.getCommandArguments(uartCommand, commandMessage);
        usbUartService.sendCommand(uartCommand, args);
    }

    @Override
    public void sendMessage(String message) {
        usbUartService.sendMessage(message);
    }

}

