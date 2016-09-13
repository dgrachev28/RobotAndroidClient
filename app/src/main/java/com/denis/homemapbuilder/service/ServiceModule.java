package com.denis.homemapbuilder.service;

import android.app.Activity;
import android.app.Application;

import com.denis.homemapbuilder.activity.MainActivity;
import com.denis.homemapbuilder.service.impl.MovementServiceImpl;
import com.denis.homemapbuilder.service.impl.UsbUartServiceImpl;
import com.denis.homemapbuilder.service.impl.WebSocketServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {
    private final Activity activity;

    public ServiceModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    MovementService provideMovementService(UsbUartService usbUartService) {
        return new MovementServiceImpl(usbUartService);
    }

    @Provides
    @Singleton
    UsbUartService provideUsbUartService() {
        return new UsbUartServiceImpl(activity);
    }

    @Provides
    @Singleton
    WebSocketService provideWebSocketService(MovementService movementService) {
        return new WebSocketServiceImpl(movementService);
    }

}
