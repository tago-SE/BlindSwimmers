package s.m.myapplication.cameraview;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.os.Handler;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CapturePicture extends CameraCaptureSession {
    @NonNull
    @Override
    public CameraDevice getDevice() {
        return null;
    }

    @Override
    public void prepare(@NonNull Surface surface) throws CameraAccessException {

    }

    @Override
    public void finalizeOutputConfigurations(List<OutputConfiguration> list) throws CameraAccessException {

    }

    @Override
    public int capture(@NonNull CaptureRequest captureRequest, @Nullable CaptureCallback captureCallback, @Nullable Handler handler) throws CameraAccessException {
        return 0;
    }

    @Override
    public int captureBurst(@NonNull List<CaptureRequest> list, @Nullable CaptureCallback captureCallback, @Nullable Handler handler) throws CameraAccessException {
        return 0;
    }

    @Override
    public int setRepeatingRequest(@NonNull CaptureRequest captureRequest, @Nullable CaptureCallback captureCallback, @Nullable Handler handler) throws CameraAccessException {
        return 0;
    }

    @Override
    public int setRepeatingBurst(@NonNull List<CaptureRequest> list, @Nullable CaptureCallback captureCallback, @Nullable Handler handler) throws CameraAccessException {
        return 0;
    }

    @Override
    public void stopRepeating() throws CameraAccessException {

    }

    @Override
    public void abortCaptures() throws CameraAccessException {

    }

    @Override
    public boolean isReprocessable() {
        return false;
    }

    @Nullable
    @Override
    public Surface getInputSurface() {
        return null;
    }

    @Override
    public void close() {

    }
}
