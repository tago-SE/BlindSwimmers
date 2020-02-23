package s.m.myapplication.model;

import android.graphics.Color;

public class RGBColor {

    private int red;
    private int green;
    private int blue;

    public RGBColor() {
        // Empty Constructor
    }

    public RGBColor(String hexColor) {
        setHex(hexColor);
    }

    public RGBColor(int intColor) {
        setHex(String.format("#%06X", (0xFFFFFF & intColor)));
    }

    /**
     * Returns a String containing the hex color code for RGB.
     *
     * @return Hex color code
     */
    public String getHex() {
        String hexRed = Integer.toHexString(red);
        if (hexRed.length() == 1)
            hexRed = "0" + hexRed;
        String hexGreen = Integer.toHexString(green);
        if (hexGreen.length() == 1)
            hexGreen = "0" + hexGreen;
        String hexBlue = Integer.toHexString(blue);
        if (hexBlue.length() == 1)
            hexBlue = "0" + hexBlue;
        return "#" + hexRed + hexGreen + hexBlue;
    }

    /**
     * Updates the RGB values based on the provided hex code. The hex code should have the following
     * appearance: #ff00ff
     *
     * @param hexColor Hex code
     */
    public void setHex(String hexColor) {
        red = Integer.valueOf(hexColor.substring(1, 3), 16);
        green = Integer.valueOf(hexColor.substring(3, 5), 16);
        blue = Integer.valueOf(hexColor.substring(5, 7), 16);
    }

    public int[] getRGB() {
        int[] rgb = new int[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;
        return rgb;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    @Override
    public String toString() {
        return "RGBColor{" +
                " red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }
}
