package s.m.myapplication.model;

import android.graphics.Color;

public class RGBColor {

    private int red;
    private int green;
    private int blue;

    private int hue;
    private int saturation;
    private int value;

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
        return new int[]{red,green,blue};
    }
    public int[] getHSV(){
        return new int[]{hue,saturation,value};
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

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RGBColor{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", hue=" + hue +
                ", saturation=" + saturation +
                ", value=" + value +
                '}';
    }
}
