package s.m.myapplication.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Camera;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import s.m.myapplication.CustomSeek;
import s.m.myapplication.MainCameraActivity;
import s.m.myapplication.R;
import s.m.myapplication.model.CameraFacade;

public class Popup {


    private Activity activity;
    private Dialog dialog;
    private CameraFacade facade;

    private CustomSeek hueListener;

    public Popup(Activity activity) {
        this.activity = activity;

        setDialog();
        findViews();

     //  hueListener = new CustomSeek();
       // setData(title, subtitle, action);
    }




    public void showDialog(CameraFacade camera){
        facade = camera;
        dialog.show();

        TextView hueText = dialog.findViewById(R.id.Hue);

        hueText.setText("Hue : " + camera.getLowerHSV().getHue());
        SeekBar hueSeek = dialog.findViewById(R.id.HueSeekId);
        hueSeek.setProgress(camera.getLowerHSV().getHue());
        hueSeek.setOnSeekBarChangeListener(hueListener);
    }

    public void dismiss(){
        dialog.dismiss();
    }



    private void setDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popuphsv);
    }

    private void findViews(){
      /*tvTitle = dialog.findViewById(R.id.tv_title);
        tvSubtitle = dialog.findViewById(R.id.tv_subtitle);
        tvAction = dialog.findViewById(R.id.tv_action);*/
    }

    private void setData(int title, int subtitle, int action) {
       /* tvTitle.setText(title);
        tvSubtitle.setText(subtitle);
        tvAction.setText(action);*/
    }


}
