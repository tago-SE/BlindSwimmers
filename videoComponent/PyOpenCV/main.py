import cv2
import numpy as np
from operator import xor
import find_roi as find_roi
from find_roi import RegionOfInterest

END_KEY = 'q'
LOWER_RED = np.array([161, 155, 84])
UPPRED_RED = np.array([179, 255, 255])


drawing = True
point1 = ()
point2 = ()

image_hsv = None   # global ;(
image_mask =None   
chosenImage = None
pixel = (20,60,80) # some stupid default

def callback(value):
    render_trackbar_values("HSV")
    pass
def mouse_drawing(event, x, y, flags, params):
    global point1, point2, drawing
    if event == cv2.EVENT_MOUSEMOVE:
        point1 = (x, y)
        point2 = (x + 20, y + 20)
def setup_trackbars(range_filter):
    cv2.namedWindow("Trackbars", 0)

    for i in ["MIN", "MAX"]:
        v = 0 if i == "MIN" else 255
        for j in range_filter:
            var = 0
            if j=="H":
                var = pixel[0]
            elif j=="S":
                var = pixel[1]
            else:
                var = pixel[2]
            if(i == "MIN"):
                if(j=="H" or j=="S"):
                    var = var-10
                else:
                    var = var -40
            if(i == "MAX"):
                if(j=="H" or j=="S"):
                    var = var+10
                else:
                    var = var +40      
            cv2.createTrackbar("%s_%s" % (j, i), "Trackbars", var, 255, callback)

def render_trackbar_values(range_filter):
    values = []
    for i in ["MIN", "MAX"]:
        for j in range_filter:
            v = cv2.getTrackbarPos("%s_%s" % (j, i), "Trackbars")
            values.append(v)
    print(values[0:3])    
    print(values[3:6])
    lower = values[0:3]
    higher = values[3:6]
    upper =  np.array([values[0], values[1], values[2]])
    lower =  np.array([values[0], values[1], values[2]])
    thresh = cv2.inRange(image_hsv, (values[0], values[1], values[2]), (values[3], values[4], values[5]))
    #image_mask = cv2.inRange(image_hsv,lower,higher)
    cv2.imshow("mask",thresh)
    #return values
def pick_color(event,x,y,flags,param):
    if event == cv2.EVENT_LBUTTONDOWN:
        pixel = image_hsv[y,x]
        #you might want to adjust the ranges(+-10, etc):
        upper =  np.array([pixel[0] + 20, pixel[1] + 20, pixel[2] + 60])
        lower =  np.array([pixel[0] - 10, pixel[1] - 10, pixel[2] - 40])
        print(pixel, lower, upper)
        #thresh = cv2.inRange(image_hsv, (v1_min, v2_min, v3_min), (v1_max, v2_max, v3_max))
        #image_mask = cv2.inRange(image_hsv,lower,upper)
        cv2.imshow("mask",image_mask)

if __name__ == "__main__":
    image_hsv,image_mask, pixel,chosenImage
    cap = cv2.VideoCapture('MOV_0750.mp4')
    
    while True:
        
        _, frame = cap.read()
        
        frame = cv2.resize(frame, (960, 540))
        copyFrame = frame.copy()
        roi = find_roi.find_region_of_interest(frame)

        mouseX = 1000
        mouseY = 255
        cv2.namedWindow('frame')
        cv2.setMouseCallback('frame', mouse_drawing)
        
        if point1 and point2:
            cv2.rectangle(frame, point1, point2, (0, 255, 0))

        if roi.is_inside(mouseX, mouseY, mouseX + 10, mouseY + 10):
            roi.draw(frame, drawContours=True, color=(0, 255, 0))
        else: 
            roi.draw(frame, drawContours=True, color=(0, 0, 255))
        
        cv2.imshow('frame', frame)  
       
        key = cv2.waitKey(1)
        if key == ord("p"):
            setup_trackbars("HSV")
            image_hsv = cv2.cvtColor(copyFrame,cv2.COLOR_BGR2HSV)
            cv2.waitKey(0) 
        if cv2.waitKey(5) == ord(END_KEY):
            break
    cap.release()
    cv2.destroyAllWindows()