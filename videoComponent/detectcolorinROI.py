import numpy as np
import cv2  

from datetime import datetime

timestamp = 1545730073
dt_object = datetime.fromtimestamp(timestamp)

print("dt_object =", dt_object)
print("type(dt_object) =", type(dt_object))

cap = cv2.VideoCapture('MOV_0748.mp4')

while True:
    _, frame = cap.read()
    frame = cv2.resize(frame, (960, 540))
    
    # Convert BGR to HSV
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    # define range of blue color in HSV
    lower_blue = np.array([100,50,50])
    upper_blue = np.array([130,255,255])
    # Threshold the HSV image to get only blue colors
    lower_red = np.array([161, 155, 84])
    upper_red = np.array([179, 255, 255])
    mask = cv2.inRange (hsv, lower_red, upper_red)
    #img = np.random.randint(0, 256, size=(200, 300, 3), dtype=np.uint8)
    height, width, channels = frame.shape
    upper_left = (int(width / 4), int(height / 4))
    bottom_right = (int(width * 3 / 4), int(height * 3 / 4))  
    rect = cv2.rectangle(frame, upper_left, bottom_right, (0, 0, 123), 2)
    height, width = rect.shape[:2]
    #print(rect.xg)

    #print(bottom_right)

    #print(height)
    colour2 = (40,255,0)

    #rect = cv2.rectangle(frame, upper_left, bottom_right, colour2 , 2)
    rect_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (5, 30))
    threshed = cv2.morphologyEx(mask.copy(), cv2.MORPH_CLOSE, rect_kernel)
    bluecnts = cv2.findContours(threshed,
                              cv2.RETR_EXTERNAL,
                              cv2.CHAIN_APPROX_SIMPLE)[-2]

    for contour in bluecnts:
    # Find bounding rectangles
       # x,y,w,h = cv2.boundingRect(contour)
        hull = cv2.convexHull(contour)
        xg,yg,wg,hg = cv2.boundingRect(hull)
    # Draw the rectangle
        if cv2.contourArea(hull) > 500:
             cv2.drawContours(frame, [hull], -1, (0, 0, 255), 1)
             cv2.rectangle(frame,(xg,yg),(xg+wg, yg+hg),(0,255,0),2)
            # print(hull)
             if xg > upper_left[0] and (xg+wg) < bottom_right[0] and yg > upper_left[1]  and (yg+hg) < bottom_right[1]:      
                rect = cv2.rectangle(frame, upper_left, bottom_right, colour2 , 2)
                cv2.putText(frame,"Hello World!!!", (100, 100), cv2.FONT_HERSHEY_SIMPLEX, 2, 255)
                print('swimmer found')
    #        if len(bluecnts)>0:
    #    blue_area = max(bluecnts, key=cv2.contourArea)
    #    (xg,yg,wg,hg) = cv2.boundingRect(blue_area)           
    #    cv2.rectangle(frame,(xg,yg),(xg+wg, yg+hg),(0,255,0),2)

    cv2.imshow('frame',frame)
    cv2.imshow('mask',mask)

    k = cv2.waitKey(5) 
    if k == 27:
        break

cap.release()
cv2.destroyAllWindows()
