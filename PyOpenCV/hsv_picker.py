import cv2
import numpy as np
import find_swimmer

image_hsv = None

def set_image(frame):
    global image_hsv
    image_hsv = frame

def callback(value):
    print("Value", value)
    render_trackbar_values("HSV")
    pass


def setup_trackbars(range_filter):
    lower = find_swimmer.get_lower_color()
    upper = find_swimmer.get_upper_color()
    cv2.namedWindow("Trackbars", 0)
    cv2.createTrackbar("H_MIN", "Trackbars", lower[0], 255, callback)
    cv2.createTrackbar("S_MIN", "Trackbars", lower[1], 255, callback)
    cv2.createTrackbar("V_MIN", "Trackbars", lower[2], 255, callback)
    cv2.createTrackbar("H_MAX", "Trackbars", upper[0], 255, callback)
    cv2.createTrackbar("S_MAX", "Trackbars", upper[1], 255, callback)
    cv2.createTrackbar("V_MAX", "Trackbars", upper[2], 255, callback)


def render_trackbar_values(range_filter):
    values = []
    for i in ["MIN", "MAX"]:
        for j in range_filter:
            v = cv2.getTrackbarPos("%s_%s" % (j, i), "Trackbars")
            print("v", v)
            values.append(v)

    lower = values[0:3]
    higher = values[3:6]
    HSV_LOWER =  np.array([lower[0], lower[1], lower[2]])
    HSV_UPPER =  np.array([higher[0], higher[1], higher[2]])
        
    find_swimmer.set_lower_color(HSV_LOWER)
    find_swimmer.set_upper_color(HSV_UPPER)

    thresh = cv2.inRange(image_hsv, (values[0], values[1], values[2]), (values[3], values[4], values[5]))
    cv2.imshow("mask",thresh)
 
