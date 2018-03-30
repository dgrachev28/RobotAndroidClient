package com.denis.homemapbuilder.model;


import java.util.ArrayList;
import java.util.List;

public enum UartCommand {
    SET_RIGHT_WHEEL_SPEED(1),
    SET_LEFT_WHEEL_SPEED(2);

    UartCommand(int code) {
        this.code = code;
    }

    private int code;
    private List<String> arguments = new ArrayList<>();

    public int getCode() {
        return code;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
