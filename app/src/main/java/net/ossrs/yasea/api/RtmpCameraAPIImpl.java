package net.ossrs.yasea.api;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncoder;
import net.ossrs.yasea.SrsMp4Muxer;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.rtmp.RtmpPublisher;

public class RtmpCameraAPIImpl implements RtmpCameraAPI {

    private final String TAG = RtmpCameraAPIImpl.class.getSimpleName();

    private SrsPublisher mPublisher;

    private SharedPreferences sp;

    public RtmpCameraAPIImpl(SrsCameraView srsCameraView, SharedPreferences sp) {
        this.sp = sp;
        mPublisher = new SrsPublisher(srsCameraView);
        mPublisher.setPublishEventHandler(new RtmpPublisher.EventHandler() {
            @Override
            public void onRtmpConnecting(final String msg) {
            }

            @Override
            public void onRtmpConnected(final String msg) {
            }

            @Override
            public void onRtmpVideoStreaming(final String msg) {
            }

            @Override
            public void onRtmpAudioStreaming(final String msg) {
            }

            @Override
            public void onRtmpStopped(final String msg) {
            }

            @Override
            public void onRtmpDisconnected(final String msg) {
            }

            @Override
            public void onRtmpOutputFps(final double fps) {
                Log.i(TAG, String.format("Output Fps: %f", fps));
            }

            @Override
            public void onRtmpVideoBitrate(final double bitrate) {
                int rate = (int) bitrate;
                if (rate / 1000 > 0) {
                    Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
                } else {
                    Log.i(TAG, String.format("Video bitrate: %d bps", rate));
                }
            }

            @Override
            public void onRtmpAudioBitrate(final double bitrate) {
                int rate = (int) bitrate;
                if (rate / 1000 > 0) {
                    Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
                } else {
                    Log.i(TAG, String.format("Audio bitrate: %d bps", rate));
                }
            }
        });

        mPublisher.setRecordEventHandler(new SrsMp4Muxer.EventHandler() {
            @Override
            public void onRecordPause(final String msg) {
            }

            @Override
            public void onRecordResume(final String msg) {
            }

            @Override
            public void onRecordStarted(final String msg) {
            }

            @Override
            public void onRecordFinished(final String msg) {
            }
        });

        mPublisher.setNetworkEventHandler(new SrsEncoder.EventHandler() {
            @Override
            public void onNetworkResume(final String msg) {
            }

            @Override
            public void onNetworkWeak(final String msg) {
            }
        });
    }


    @Override
    public void startRecord(String rtmpUrl) {

        Log.i(TAG, String.format("RTMP URL changed to %s", rtmpUrl));
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rtmpUrl", rtmpUrl);
        editor.apply();

        mPublisher.setPreviewResolution(1280, 720);
        mPublisher.setOutputResolution(384, 640);
        mPublisher.setVideoSmoothMode();
        mPublisher.startPublish(rtmpUrl);

    }

    @Override
    public void stopRecord() {
        mPublisher.stopPublish();
        mPublisher.stopRecord();
    }

    @Override
    public void switchCamera() {
        if (Camera.getNumberOfCameras() > 0) {
            mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());
        }
    }

    @Override
    public void switchEncoder(String encoder) {
        if (encoder.equals("soft")) {
            mPublisher.swithToSoftEncoder();
        } else if (encoder.equals("hard")) {
            mPublisher.swithToHardEncoder();
        }
    }
}
