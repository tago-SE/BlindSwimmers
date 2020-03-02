package s.m.myapplication;

import android.widget.SeekBar;

import s.m.myapplication.model.CameraFacade;

public class CustomSeek implements SeekBar.OnSeekBarChangeListener {
    CameraFacade cameraFacade;

    public CustomSeek(CameraFacade cameraFacade) {
        this.cameraFacade = cameraFacade;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
