package s.m.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private static String TEST = "TEST";
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.camera);
        button.setOnClickListener(this);
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loader success.");
        } else {
            Log.e(TAG, "OpenCV loader failure.");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.camera : gotoCameraActivity();break;
            default: break;
        }
    }
    private void gotoCameraActivity(){
        Intent intent = new Intent(this, ShowCameraActivity.class);
        startActivity(intent);
    }
}
