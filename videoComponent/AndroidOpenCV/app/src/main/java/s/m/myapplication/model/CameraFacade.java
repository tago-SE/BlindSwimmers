package s.m.myapplication.model;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class CameraFacade {

    private static final CameraFacade ourInstance = new CameraFacade();


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

    public void setRegionOfInterestStartPoint(int x, int y) {
        regionOfInterest.setStartPoint(x, y);
    }

    public void setLowerColorLimit(int red, int green, int blue) {
        // TODO: Not yet implemented
    }

    public void setUpperColorLimit(int red, int green, int blue) {
        // TODO: Not yet implemented
    }

}