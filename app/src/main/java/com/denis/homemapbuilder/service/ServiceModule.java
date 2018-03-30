package com.denis.homemapbuilder.service;

import android.app.Activity;

import com.denis.homemapbuilder.component.CommandMessage2UartCommandConverter;
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
    CommandMessage2UartCommandConverter provideCommandMessage2UartCommandConverter() {
        return new CommandMessage2UartCommandConverter();
    }

    @Provides
    @Singleton
    MovementService provideMovementService(UsbUartService usbUartService,
                                           CommandMessage2UartCommandConverter commandMessage2UartCommandConverter) {
        return new MovementServiceImpl(usbUartService, commandMessage2UartCommandConverter);
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
