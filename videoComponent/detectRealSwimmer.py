import cv2
import numpy as np

cap = cv2.VideoCapture('MOV_0748.mp4')
lower_red = np.array([161, 155, 84])
upper_red = np.array([179, 255, 255])
while True:
    _, frame = cap.read()
    frame = cv2.resize(frame, (960, 540))
    #Convert the frames to hsv
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    #Configure kernel
    convince = cv2.getStructuringElement(cv2.MORPH_RECT,(5,5))
    blurred_frameHSV = cv2.GaussianBlur(hsv, (5, 5), 0)
    dilated = cv2.dilate(blurred_frameHSV,convince,5)
    #Mask the dilated range
    mask = cv2.inRange(dilated, lower_red, upper_red)

    rect_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (5, 30))
    threshed = cv2.morphologyEx(mask.copy(), cv2.MORPH_CLOSE, rect_kernel)
    contourTree = cv2.findContours(threshed,
                              cv2.RETR_EXTERNAL,
                              cv2.CHAIN_APPROX_SIMPLE)[-2]
    contourTree = sorted(contourTree, key=cv2.contourArea) 
    for contour in contourTree:
        if contour is not (contourTree[len(contourTree)-1]) and contour is not (contourTree[len(contourTree)-2]) :    
                hull = cv2.convexHull(contour)
                if cv2.contourArea(hull) > 750:
                    xg,yg,wg,hg = cv2.boundingRect(hull)
                    cv2.drawContours(frame, [hull], -1, (0, 0, 255), 1)
                    cv2.rectangle(frame,(xg,yg),(xg+wg, yg+hg),(0,255,0),2)
                    #cv2.rectangle(frame,(xg,yg),(xg+wg, yg+hg),(0,255,0),2)
        
    cv2.imshow('mask',frame)

    k = cv2.waitKey(5) 
    if k == 27:
        break
    
cap.release()
cv2.destroyAllWindows()