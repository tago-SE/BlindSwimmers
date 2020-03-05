import cv2
import numpy as np
from shapely.geometry import Polygon
from find_roi import RegionOfInterest

# define color range for the side lane
LOWER_COLOR = np.array([161, 155, 84])
UPPER_COLOR = np.array([179, 255, 255])

SWIM_COLOR = (55, 55, 55)
SWIM_THICKNESS = 2
LANE_INTERSECTION_THRESHOLD = - 30 

class Swimmer:
    
    def __init__(self, x, y, w, h):
        self.x = x
        self.y = y 
        self.w = w 
        self.h = h

    def draw(self, frame, color=SWIM_COLOR, thickness=SWIM_THICKNESS): 
        cv2.rectangle(frame,(self.x, self.y),(self.x + self.w, self.y + self.h), color, thickness)
    

# Takes a frame and the roi returns a swimmer object 
def find_swimmer(frame, roi):
    
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    
    #Clear out the noise using Gaussian Blur Algorithm
    blurred_frameHSV = cv2.GaussianBlur(hsv, (5, 5), 0)

    #Define kernel
    convince = cv2.getStructuringElement(cv2.MORPH_RECT, (5,5))
    
    #Dilate the image
    dilated = cv2.dilate(blurred_frameHSV,convince,5)

    #Mask the dilated range
    mask = cv2.inRange(dilated, LOWER_COLOR, UPPER_COLOR)

    contours, hierarchy = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)

    swim_x1 = 99999999 
    swim_y1 = 99999999 
    swim_x2 = -99999999 
    swim_y2 = -99999999 
    swimmer_found = False 

    for contour in contours:
        hull = cv2.convexHull(contour)
        x, y, w, h = cv2.boundingRect(hull)

        # This part filters away contours that does not have 3 out of 4 points inside the ROI 
        if roi is not None:
            inside_point_count = 0
            if roi.point_is_inside(x, y):
                inside_point_count = inside_point_count + 1
            if roi.point_is_inside(x + w, y):
                inside_point_count = inside_point_count + 1
            if roi.point_is_inside(x + w, y + h):
                inside_point_count = inside_point_count + 1
            if inside_point_count < 3 and roi.point_is_inside(x, y + h):
                inside_point_count = inside_point_count + 1
        else: 
            inside_point_count = 4 #since
            
        if inside_point_count >= 3: 
            swimmer_found = True 
            if x < swim_x1: 
                swim_x1 = x
            if y < swim_y1: 
                swim_y1 = y
            if (x + w) > swim_x2:
                swim_x2 = x + w
            if (y + h) > swim_y2: 
                swim_y2 = y + h
        
    if swimmer_found: 
        return Swimmer(swim_x1, swim_y1, swim_x2 - swim_x1, swim_y2 - swim_y1)
    else:
        return None 
