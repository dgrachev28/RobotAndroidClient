package com.denis.homemapbuilder;


import com.denis.homemapbuilder.activity.MainActivity;
import com.denis.homemapbuilder.service.ServiceModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {ServiceModule.class})
@Singleton
public interface AppComponent {
    void inject(MainActivity mainActivity);
}
