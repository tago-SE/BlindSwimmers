import cv2
import numpy as np

# define red color range for the side lane
LOWER_COLOR = np.array([161, 155, 84])
UPPER_COLOR = np.array([179, 255, 255])

ROI_MARGIN = 12
ROI_COLOR = (55, 55, 55)
ROI_THICKNESS = 2
LANE_INTERSECTION_THRESHOLD = -20 

class RegionOfInterest:

    def __init__(self, left_hull, right_hull):
        self.x = left_hull["x"] + left_hull["w"] + ROI_MARGIN
        self.y = left_hull["y"] if left_hull["y"] > right_hull["y"] else right_hull["y"]
        self.w = right_hull["x"] - (left_hull["x"] + left_hull["w"]) - 2*ROI_MARGIN
        self.h = left_hull["h"] if left_hull["h"] > right_hull["h"] else right_hull["h"]
        self.topLeftCorner = (self.x, self.y)
        self.topRightCorner = (self.x + self.w, self.y)
        self.botLeftCorner = (left_hull["x"], self.y + self.h)
        self.botRightCorner = (right_hull["x"] + right_hull["w"], self.y + self.h)
        self.leftHull = left_hull["hull"]
        self.rightHull = right_hull["hull"]

        # This section will adjust the bottom corners in case they intersect with the left and right contours 
        dist = - 100
        while True: 
            dist = cv2.pointPolygonTest(self.leftHull, self.botLeftCorner, True)
            if dist > LANE_INTERSECTION_THRESHOLD: 
                (x, y) = self.botLeftCorner
                self.botLeftCorner = (x + 15, y)
            else: break
        while True: 
            dist = cv2.pointPolygonTest(self.rightHull, self.botRightCorner, True)
            if dist > LANE_INTERSECTION_THRESHOLD: 
                (x, y) = self.botRightCorner
                self.botRightCorner = (x - 15, y)
            else: break



    def draw(self, frame, color=ROI_COLOR, thickness=ROI_THICKNESS, drawContours=False): 
        cv2.line(frame, (self.x, self.y), (self.x + self.w, self.y), color, thickness) 
        cv2.line(frame, self.topLeftCorner, self.botLeftCorner, color, thickness) 
        cv2.line(frame, self.topRightCorner, self.botRightCorner, color, thickness) 
        if drawContours:
            cv2.drawContours(frame, [self.leftHull], -1, (0, 0, 255), 1)


    # Returns true if (x,y) is inside the region of interest
    def is_point_inside(self, x, y):
        return True 


def find_region_of_interest(frame):
    
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    
    #Clear out the noise using Gaussian Blur Algorithm
    blurred_frameHSV = cv2.GaussianBlur(hsv, (5, 5), 0)

    #Define kernel
    convince = cv2.getStructuringElement(cv2.MORPH_RECT,(5,5))
    
    #Dilate the image
    dilated = cv2.dilate(blurred_frameHSV,convince,5)

    #Mask the dilated range
    mask = cv2.inRange(dilated, LOWER_COLOR, UPPER_COLOR)

    contours, hierarchy = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
    #print(len(contours))

    hulls = __get_largest_hulls(contours, sorted=True)
    if (len(hulls) < 2):
        print("ERROR: Lost one or more of the lanes")
        exit()  # this is just for debugging, should probably be removed

    hulls = hulls[:2] ## need to test what would happen if it found 0 or 1...

    left, right = __left_right_sort(hulls[0], hulls[1])

    roi = RegionOfInterest(left, right)

    # DEBUG: show mask and frame on separate screen
    #   cv2.imshow('mask', mask)

    return roi
      

def __get_largest_hulls(contours, sorted=True):
    hulls = []
    for contour in contours:
        hull = cv2.convexHull(contour)
        x, y, w, h = cv2.boundingRect(hull)
        area = w*h
        if area > 12000:
            hulls.append({"contour": contour, "hull": hull, "area": area, "x": x, "y": y, "w": w, "h": h})
    if (sorted):
        hulls.sort(key=lambda dct: dct['area'], reverse=True)
    return hulls


def __left_right_sort(hull_dict_a, hull_dict_b):
    if (hull_dict_b["x"] <  hull_dict_a["x"]):
        return hull_dict_b, hull_dict_a
    return hull_dict_a, hull_dict_b