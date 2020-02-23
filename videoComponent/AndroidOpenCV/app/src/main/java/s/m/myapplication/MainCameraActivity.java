package s.m.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import s.m.myapplication.model.CameraFacade;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.Arrays;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

/**
 *
 * Ref: Detecting Gestures:
 *  https://medium.com/@ali.muzaffar/android-detecting-a-pinch-gesture-64a0a0ed4b41
 *  https://www.techotopia.com/index.php/Android_Pinch_Gesture_Detection_Tutorial_using_Android_Studio
 */
public class MainCameraActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2 {

    private static String TAG = MainActivity.class.getSimpleName();

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;


    private Mat mHsv;
    private Mat mask;

    // Manages the configurations for the camera
    private CameraFacade camera = CameraFacade.getInstance();

    private double selectedX;
    private double selectedY;

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
        int screenWidth = size.x;
        int screenHeight = size.y;
        Log.w(TAG, "screenWidth=" + screenWidth);
        Log.w(TAG, "screenHeight=" + screenHeight);

        camera.setScreenDimensions(screenWidth, screenHeight);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                Log.w(TAG, "onTouchEvent:ACTION_DOWN");
                selectedX = event.getX();
                selectedY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.w(TAG, "onTouchEvent:ACTION_MOVE");
                camera.setRegionOfInterestStartPoint((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.w(TAG, "onTouchEvent:ACTION_CANCEL");
                break;
        }
        return true;
    }

    /*
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        Log.w(TAG, "onTouch: " + v + ",  \n" + e.toString());
        return false;
    }
*/


    /**
     * This method is invoked when camera preview has started and upon screen rotation. The frame
     * width changes depending on if the device is in LANDS_SCAPE_MODE or PORTRAIT_MODE.
     *
     * @param width -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) { ;
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);


        mHsv = new Mat(width, height, CvType.CV_8UC1);
        mask = new Mat(width, height, CvType.CV_8UC1);
        //mHsv = new Mat(width, height, CvType.CV_8UC1);
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
        Imgproc.cvtColor(mRgba, mHsv, Imgproc.COLOR_RGB2HSV);
        Log.i("ARR","From " + selectedX + " " + selectedY);
        Log.i("ARR","From mRgba " + Arrays.toString(mRgba.get((int)selectedX,(int)selectedY)));
        Scalar scalarLow = new Scalar(35,20,10);
        Scalar scalarHigh = new Scalar(75,255,255);
        Core.inRange(mHsv,scalarLow,scalarHigh,mask);
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

}

 /*   array = mRgba.get((int)selectedX,(int)selectedY);
           Log.i("ARR",Arrays.toString(array));
           /*
             lower_blue = np.array([100,50,50])
             upper_blue = np.array([130,255,255])

              scalarLow  = new Scalar(array[0]-75,array[1]-75,array[2]-75);
           scalarHigh = new Scalar(array[0]+75,array[1]+75,array[2]-75);

            upper =  np.array([pixel[0] + 10, pixel[1] + 10, pixel[2] + 40])
        lower =  np.array([pixel[0] - 10, pixel[1] - 10, pixel[2] - 40])
            */
           /*scalarLow  = new Scalar(array[0]-10,array[1]-10,array[2]-40);
           scalarHigh = new Scalar(array[0]+10,array[1]+10,array[2]+40);
           Core.inRange(mHsv,scalarLow,scalarHigh,mask);
           isPixelTouched = false;
           keepTouched = true;
           return mask;*/