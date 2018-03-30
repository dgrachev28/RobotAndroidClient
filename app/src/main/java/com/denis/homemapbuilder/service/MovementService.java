package com.denis.homemapbuilder.service;

import com.denis.homemapbuilder.model.CommandMessage;

public interface MovementService {

    void sendMessage(CommandMessage commandMessage);

    void sendMessage(String message);
}
