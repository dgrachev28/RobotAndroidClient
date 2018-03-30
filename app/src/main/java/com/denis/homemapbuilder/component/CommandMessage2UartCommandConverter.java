package com.denis.homemapbuilder.component;


import com.denis.homemapbuilder.model.CommandMessage;
import com.denis.homemapbuilder.model.EngineDirection;
import com.denis.homemapbuilder.model.UartCommand;

import java.util.ArrayList;
import java.util.List;

import static com.denis.homemapbuilder.model.UartCommand.SET_LEFT_WHEEL_SPEED;
import static com.denis.homemapbuilder.model.UartCommand.SET_RIGHT_WHEEL_SPEED;

public class CommandMessage2UartCommandConverter {

    public UartCommand convert(CommandMessage commandMessage) {
        return UartCommand.valueOf(commandMessage.getCommand());
    }

    public List<String> getCommandArguments(UartCommand uartCommand, CommandMessage commandMessage) {
        List<String> result = new ArrayList<>();
        if (uartCommand == SET_LEFT_WHEEL_SPEED || uartCommand == SET_RIGHT_WHEEL_SPEED) {
            List<Object> args = commandMessage.getArguments();
            if (args.size() != 2) {
                throw new IllegalArgumentException("Arguments size should be 2. Actual size = " + args.size());
            }
            EngineDirection direction = EngineDirection.valueOf(args.get(0).toString());
            if (direction == EngineDirection.FORWARD) {
                result.add("0");
            } else {
                result.add("1");
            }
            result.add(String.valueOf(((Double)args.get(1)).intValue()));
        }
        return result;
    }
}
