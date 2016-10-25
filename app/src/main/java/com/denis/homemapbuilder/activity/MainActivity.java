package com.denis.homemapbuilder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.denis.homemapbuilder.AppComponent;
import com.denis.homemapbuilder.DaggerAppComponent;
import com.denis.homemapbuilder.R;
import com.denis.homemapbuilder.service.MovementService;
import com.denis.homemapbuilder.service.ServiceModule;
import com.denis.homemapbuilder.service.impl.MovementServiceImpl;

import javax.inject.Inject;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.api.RtmpCameraAPI;
import net.ossrs.yasea.api.RtmpCameraAPIImpl;

public class MainActivity extends Activity {

    private final String CLASS_TAG = MainActivity.class.getName();

    @Inject
    MovementService movementService;

    public RtmpCameraAPI rtmpCameraAPI;

    private void init() {
        AppComponent appComponent = DaggerAppComponent.builder().serviceModule(new ServiceModule(this)).build();
        appComponent.inject(this);
//        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        movementService = new MovementServiceImpl(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.editText);
        configButton(textView);

        SrsCameraView srsCameraView = (SrsCameraView) findViewById(R.id.preview);
        rtmpCameraAPI = new RtmpCameraAPIImpl(srsCameraView, getSharedPreferences("Yasea", MODE_PRIVATE));
    }

    private void configButton(final TextView textView) {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rtmpCameraAPI.startRecord("rtmp://192.168.1.154:1935/myapp/mystream");
                if (!textView.getText().toString().equals("")) {
                    byte b = (byte) Integer.parseInt(textView.getText().toString());
                    if (movementService instanceof MovementServiceImpl) {
                        ((MovementServiceImpl) movementService).publicSendMessage(b);
                    } else {
                        throw new RuntimeException("movementService неверного типа");
                    }
                }
            }
        });
    }

}
