package net.ossrs.yasea.api;

public interface RtmpCameraAPI {

    void startRecord(String rtmpUrl);
    void stopRecord();
    void switchCamera();


    /**
     *
     * @param encoder Two values: "soft", "hard"
     */
    void switchEncoder(String encoder);

}
