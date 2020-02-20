package s.m.myapplication.model;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class RegionOfInterest {

    private int width;
    private int height;
    private Point startPoint;
    private Point endPoint;
    private Scalar scalar = new Scalar(0, 0, 255);
    private int thickness = 10;

    public RegionOfInterest(int x, int y, int w, int h) {
        startPoint = new Point(x, y);
        width = w;
        height = h;
        endPoint = new Point(x + w, y + h);
    }

    private void updateEndPoint() {
        endPoint.x = startPoint.x + width;
        endPoint.y = startPoint.y + height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        updateEndPoint();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        updateEndPoint();
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int x, int y) {
        this.startPoint.x = x;
        this.startPoint.y = y;
        updateEndPoint();
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public Scalar getScalar() {
        return scalar;
    }

    public void setScalar(Scalar scalar) {
        this.scalar = scalar;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }
}