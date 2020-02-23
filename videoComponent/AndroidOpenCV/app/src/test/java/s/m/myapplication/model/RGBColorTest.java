package s.m.myapplication.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class RGBColorTest {

    @Test
    public void Constructors() {
        int intColor = -25200;
        RGBColor rgb = new RGBColor(intColor);
        assertEquals(rgb.getRed(), 255);
        assertEquals(rgb.getGreen(), 157);
        assertEquals(rgb.getBlue(), 144);
    }

    @Test
    public void toHex() {
        String hex = "#ff9d90";
        RGBColor rgb = new RGBColor(hex);
        assertEquals(hex, rgb.getHex());
        rgb = new RGBColor();
        rgb.setRed(15);
        assertEquals("#0f0000", rgb.getHex());
    }

}