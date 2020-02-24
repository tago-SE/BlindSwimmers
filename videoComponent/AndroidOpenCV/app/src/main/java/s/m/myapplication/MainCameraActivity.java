package s.m.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import s.m.myapplication.model.CameraFacade;
import top.defaults.colorpicker.ColorPickerPopup;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        CameraBridgeViewBase.CvCameraViewListener2,CameraBridgeViewBase.OnTouchListener {

    private static String TAG = MainActivity.class.getSimpleName();

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;

    private Button resizeButton;
    private Button colorHighButton;
    private Button colorLowButton;
    private int screenWidth;
    private int screenHeight;

    private final Context context = this;

    private Mat mHsv;
    private Mat mask;

    // Manages the configurations for the camera
    private CameraFacade camera = CameraFacade.getInstance();

    private double selectedX;
    private double selectedY;

    private double x = -1;
    private double y = -1;

    private Scalar mBlobColorHsv;
    private Scalar mBlobColorRgba;


    private  TextView touch_coordinates;
    private  TextView touch_color;


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
        resizeButton = findViewById(R.id.resizeButton);
        resizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextDialog();
            }
        });
        colorHighButton = findViewById(R.id.colorHighButton);
        colorHighButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker(v);
            }
        });
        colorLowButton = findViewById(R.id.colorLowButton);
        colorLowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker(v);
            }
        });


        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        touch_color = (TextView) findViewById(R.id.touch_color);
    }

    private void showColorPicker(final View v) {
        String hex = null;
        if (v.equals(colorHighButton)) {
            hex = camera.getUpperColorLimitHex();
        } else if (v.equals(colorLowButton)) {
            hex = camera.getLowerColorLimitHex();
        }
        int color = Color.parseColor(hex);
        new ColorPickerPopup.Builder(context)
                .initialColor(color)
                .enableBrightness(false) // Enable brightness slider or not
                .enableAlpha(false)     // Enable alpha slider or not
                .okTitle(getString(android.R.string.yes))
                .cancelTitle(getString(android.R.string.no))
                .showIndicator(true)
                .showValue(false)
                .build()
                .show(v, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        if (v.equals(colorHighButton)) {
                            camera.setUpperColorLimit(color);
                        } else if (v.equals(colorLowButton)) {
                            camera.setLowerColorLimit(color);
                        }
                    }

                    @Override
                    public void onColor(int color, boolean fromUser) {
                        // Not sure, who cares
                    }
                });
    }

    private void showTextDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText widthText = new EditText(this);
        widthText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_NORMAL);
        widthText.setHint("" + camera.getROIWidth());
        TextView label = new TextView(this);
        label.setText("Width");
        layout.addView(label);
        layout.addView(widthText);

        final EditText heightText = new EditText(this);
        heightText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_NORMAL);
        heightText.setHint("" + camera.getROIHeight());
        label = new TextView(this);
        label.setText("Height");
        layout.addView(label);
        layout.addView(heightText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Dimensions");
        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    camera.setROIWidth(Integer.parseInt(widthText.getText().toString()));
                } catch(NumberFormatException e ) {
                    // No changes
                }
                try {
                    String w = widthText.getText().toString();
                    camera.setROIHeight(Integer.parseInt(heightText.getText().toString()));
                } catch(NumberFormatException e ) {
                    // No changes
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
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
        ViewGroup.LayoutParams params = resizeButton.getLayoutParams();
        params.width = (screenWidth - width)/2;
        resizeButton.setLayoutParams(params);

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
        /*Imgproc.rectangle(mRgba,
                camera.getRegionOfInterestStartPoint(),
                camera.getRegionOfInterestEndPoint(),  new Scalar(0, 0, 255), 10);

        Imgproc.cvtColor(mRgba, mHsv, Imgproc.COLOR_RGB2HSV);
        Log.i("ARR","From " + selectedX + " " + selectedY);
        Log.i("ARR","From mRgba " + Arrays.toString(mRgba.get((int)selectedX,(int)selectedY)));


        Scalar scalarLow = new Scalar(35,20,10);
        Scalar scalarHigh = new Scalar(75,255,255);
        Core.inRange(mHsv, getLowerScalar(), getHighScalar(), mask);
        return mRgba;*/
        getLowerScalar();
        getHighScalar();
        return mRgba;
    }

    private Scalar getLowerScalar() {
        int[] rgb = camera.getLowerRGB();
        return new Scalar(rgb[0], rgb[1], rgb[2]);
    }

    public Scalar getHighScalar() {
        int[] rgb = camera.getUpperRGB();
        return new Scalar(rgb[0], rgb[1], rgb[2]);
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
    public boolean onTouch(View view, MotionEvent motionEvent) {

        Log.i("TOUCH", "on onTouch");

        int cols = mRgba.cols();
        int rows = mRgba.rows();


        double yLow = (double)mOpenCvCameraView.getHeight() * 0.2401961;
        double yHigh = (double)mOpenCvCameraView.getHeight() * 0.7696078;

        double xScale = (double)cols / (double)mOpenCvCameraView.getWidth();
        double yScale = (double)rows / (yHigh - yLow);

        x = motionEvent.getX();
        y = motionEvent.getY();


        y = y - yLow;

        x = x * xScale;
        y = y * yScale;


        if((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        touch_coordinates.setText("X: " + Double.valueOf(x) + ", Y: " + Double.valueOf(y));


        Rect touchedRect = new Rect();

        touchedRect.x = (int)x;
        touchedRect.y = (int)y;

        touchedRect.width = 8;
        touchedRect.height = 8;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;


        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = convertScalarHsv2Rgba(mBlobColorHsv);


        touch_color.setText("Color: #" + String.format("%02X", (int)mBlobColorRgba.val[0])
                + String.format("%02X", (int)mBlobColorRgba.val[1])
                + String.format("%02X", (int)mBlobColorRgba.val[2]));

        touch_color.setTextColor(Color.rgb((int) mBlobColorRgba.val[0],
                (int) mBlobColorRgba.val[1],
                (int) mBlobColorRgba.val[2]));
        touch_coordinates.setTextColor(Color.rgb((int)mBlobColorRgba.val[0],
                (int)mBlobColorRgba.val[1],
                (int)mBlobColorRgba.val[2]));



        return false;
    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
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