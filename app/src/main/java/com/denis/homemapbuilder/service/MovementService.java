package com.denis.homemapbuilder.service;

import com.denis.homemapbuilder.model.EngineType;

public interface MovementService {

    void startEngine(int speed, EngineType engineType);
    void stopEngine(EngineType engineType);

    void moveForward(int speed);
    void moveBackward(int speed);

    void turnAroundLeft(int speed);
    void turnAroundRight(int speed);

}
