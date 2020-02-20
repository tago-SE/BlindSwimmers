package s.m.myapplication.model;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class Camera {
    private static final Camera ourInstance = new Camera();

    public static Camera getInstance() {
        return ourInstance;
    }

    private RegionOfInterest regionOfInterest;

    private Camera() {
        regionOfInterest = new RegionOfInterest(0, 0, 300, 300);
    }

    public Point getRegionOfInterestStartPoint() {
        return regionOfInterest.getStartPoint();
    }

    public Point getRegionOfInterestEndPoint() {
        return regionOfInterest.getEndPoint();
    }

    public void setRegionOfInterestStartPoint(int x, int y) {
        regionOfInterest.setStartPoint(x, y);
    }



}
