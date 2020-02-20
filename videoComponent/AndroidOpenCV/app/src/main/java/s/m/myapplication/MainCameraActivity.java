package s.m.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import s.m.myapplication.model.CameraFacade;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

public class MainCameraActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener, View.OnDragListener {

    private static String TAG = MainActivity.class.getSimpleName();

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;

    private int screenWidth;
    private int screenHeight;

    // Manages the configurations for the camera
    private CameraFacade camera = CameraFacade.getInstance();

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            Log.w(TAG, "onManagerConnected " + status);
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        // We lock it to landscape mode so that Region Of Interest calculations are not disturbed...
        setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        Log.w(TAG, "screenWidth=" + screenWidth);
        Log.w(TAG, "screenHeight=" + screenHeight);

        camera.setScreenDimensions(screenWidth, screenHeight);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(this);
    }

    /**
     * This method is invoked when camera preview has started and upon screen rotation. The frame
     * width changes depending on if the device is in LANDS_SCAPE_MODE or PORTRAIT_MODE.
     *
     * @param width -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.w(TAG, "onCameraViewStarted:" +
                " width=" + width + ", height=" + height);
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        camera.setFrameDimensions(width, height);
    }


    @Override
    public void onCameraViewStopped()
    {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        switch (mOpenCvCameraView.getDisplay().getRotation()) {
            case Surface.ROTATION_0: // Vertical portrait
                Core.transpose(mRgba, mRgbaT);
                Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
                Core.flip(mRgbaF, mRgba, 1);
                break;
            case Surface.ROTATION_90: // 90° anti-clockwise
                break;
            case Surface.ROTATION_180: // Vertical anti-portrait
                Core.transpose(mRgba, mRgbaT);
                Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
                Core.flip(mRgbaF, mRgba, 0);
                break;
            case Surface.ROTATION_270: // 90° clockwise
                Imgproc.resize(mRgba, mRgbaF, mRgbaF.size(), 0,0, 0);
                Core.flip(mRgbaF, mRgba, -1);
                break;
            default:
        }

        // Render Region Of Interest

        Imgproc.rectangle(mRgba,
                camera.getRegionOfInterestStartPoint(),
                camera.getRegionOfInterestEndPoint(),  new Scalar(0, 0, 255), 10);


        // Rect roi = new Rect(200, 200, 400, 400);
        // Mat cropped = new Mat(mRgba, roi);

        return mRgba;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.w(TAG, "onTouch: " + v + ",  \n" + event.toString());
        camera.setRegionOfInterestStartPoint((int) event.getX(), (int) event.getY());
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.w(TAG, "onDrag: " + v + ", " + event.toString());
        //camera.setRegionOfInterestStartPoint((int) event.getX(), (int) event.getY());
        return false;
    }
}
