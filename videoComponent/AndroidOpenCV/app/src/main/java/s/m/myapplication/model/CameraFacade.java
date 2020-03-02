package s.m.myapplication.model;

import android.graphics.Region;
import android.util.Log;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class CameraFacade {

    private static String TAG = CameraFacade.class.getSimpleName();

    private static final CameraFacade ourInstance = new CameraFacade();

    private Rect frameRect = new Rect();
    private Rect screenRect = new Rect();

    private int frameScreenHeightDiff;
    private int frameScreenWidthDiff;

    private RGBColor lowerColor = new RGBColor();
    private RGBColor upperColor = new RGBColor();

    private RGBColor lowerHSV = new RGBColor();
    private RGBColor higherHSV = new RGBColor();

    public static CameraFacade getInstance() {
        return ourInstance;
    }

    private RegionOfInterest regionOfInterest;

    private CameraFacade() {
        regionOfInterest = new RegionOfInterest(0, 0, 300, 300);
    }

    public Point getRegionOfInterestStartPoint() {
        return regionOfInterest.getStartPoint();
    }

    public Point getRegionOfInterestEndPoint() {
        return regionOfInterest.getEndPoint();
    }

    public int getROIWidth() {
        return regionOfInterest.getWidth();
    }

    public int getROIHeight() {
        return regionOfInterest.getHeight();
    }

    private boolean visionIsBlackWhite;

    public void setROIWidth(int w) {
        regionOfInterest.setWidth(w);
        onDimensionChange();
    }

    public void setROIHeight(int h) {
        regionOfInterest.setHeight(h);
        onDimensionChange();
    }

    public void setRegionOfInterestStartPoint(int x, int y) {
        int newX =  x - frameScreenWidthDiff;
        int newY = y - frameScreenHeightDiff;
        if (newX < 0) {
            newX = 0;
        }
        else if (newX > screenRect.width - 2*frameScreenWidthDiff - regionOfInterest.getWidth()) {
            newX = screenRect.width - 2*frameScreenWidthDiff - regionOfInterest.getWidth();
        }
        if (newY < 0) {
            newY = 0;
        }
        else if (newY > screenRect.height - 2*frameScreenHeightDiff - regionOfInterest.getHeight()) {
            newY = screenRect.height - 2*frameScreenHeightDiff - regionOfInterest.getHeight();
        }
        regionOfInterest.setStartPoint(newX, newY);
    }

    public void setLowerColorLimit(int color) {
        lowerColor = new RGBColor(color);
        Log.w(TAG, "upperColorLimit: " + lowerColor.toString());
    }

    public void setUpperColorLimit(int color) {
        upperColor = new RGBColor(color);
        Log.w(TAG, "upperColorLimit: " + upperColor.toString());
    }

    public String getLowerColorLimitHex() {
        return lowerColor.getHex();
    }

    public String getUpperColorLimitHex() {
        return upperColor.getHex();
    }

    public int[] getUpperRGB() {
        return upperColor.getRGB();
    }

    public int[] getLowerRGB() {
        return lowerColor.getRGB();
    }

    public void setFrameDimensions(int width, int height) {
        frameRect.width = width;
        frameRect.height = height;
        onDimensionChange();
    }

    public void setScreenDimensions(int width, int height) {
        screenRect.width = width;
        screenRect.height = height;
        onDimensionChange();
    }



    private void onDimensionChange() {
        frameScreenWidthDiff = Math.abs(screenRect.width/2 - frameRect.width/2);
        frameScreenHeightDiff = Math.abs(screenRect.height/2 - frameRect.height/2);
        frameRect.x = frameScreenWidthDiff;
        frameRect.y = frameScreenHeightDiff;
        //minX = frameScreenWidthDiff;
        //maxX = frameScreenWidthDiff + frameRect.width - regionOfInterest.getWidth()/2;
    }

    public boolean isVisionIsBlackWhite() {
        return visionIsBlackWhite;
    }

    public void setVisionIsBlackWhite(boolean visionIsBlackWhite) {
        this.visionIsBlackWhite = visionIsBlackWhite;
    }

    public void fillScalar(Scalar scalar){
        this.lowerColor.setRed ((int)scalar.val[0]-5);
        this.lowerColor.setBlue((int)scalar.val[1]-5);
        this.lowerColor.setGreen((int)scalar.val[2]-10);

        this.upperColor.setRed((int)scalar.val[0]+5);
        this.upperColor.setBlue((int)scalar.val[1]+5);
        this.upperColor.setGreen((int)scalar.val[2]+10);
    }

    public RGBColor getLowerHSV() {
        return lowerHSV;
    }

    public Scalar getScalarLowerHSV(){
        int [] hsv = this.lowerHSV.getHSV();
        return new Scalar(hsv[0],hsv[1],hsv[2]);
    }
    public Scalar getScalarHigher(){
        int [] hsv = this.higherHSV.getHSV();
        return new Scalar(hsv[0],hsv[1],hsv[2]);
    }


    public void setHSV(Scalar mBlobColorHsv) {
        this.lowerHSV.setHue((int)mBlobColorHsv.val[0]);
        this.lowerHSV.setSaturation((int)mBlobColorHsv.val[1]);
        this.lowerHSV.setValue((int)mBlobColorHsv.val[2]);
    }
}