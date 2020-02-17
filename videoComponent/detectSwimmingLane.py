import cv2
import numpy as np

#Read raw image
rawImage = cv2.imread('anotherpool.jpg',1)
#Convert Image to HSV
hsv = cv2.cvtColor(rawImage,cv2.COLOR_BGR2HSV)
#Clear out the noise using Gaussian Blur Algorithm
blurred_frameHSV = cv2.GaussianBlur(hsv, (5, 5), 0)
#Define red color range
lower_red = np.array([155,25,0])
upper_red = np.array([179,255,255])
#Define kernel
convince = cv2.getStructuringElement(cv2.MORPH_RECT,(5,5))
#Dilate the image
dilated = cv2.dilate(blurred_frameHSV,convince,5)
#Mask the dilated range
mask = cv2.inRange(dilated, lower_red, upper_red)
#find Contours
contours, hierarchy = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
cs = sorted(contours, key=cv2.contourArea) 
if len(contours) != 0:
    cv2.drawContours(mask,contours,-1,255)
    c = max(contours, key = cv2.contourArea)
    x,y,w,h = cv2.boundingRect(cs[len(cs)-1])
    x2,y2,w2,h2 = cv2.boundingRect(cs[len(cs)-2])
    if x > x2 :
        rightX,rightY,rightW,rightH = x,y,w,h
        leftX,leftY,leftW,leftH = x2,y2,w2,h2
    else:
        leftX,leftY,leftW,leftH = x,y,w,h
        rightX,rightY,rightW,rightH = x2,y2,w2,h2
    #print(len(cs))
    #print(c)
    #draw the biggest contour (c) in green
   # cv2.rectangle(rawImage,(x,y),(x+w,y+h),(222,255,155),2)
    cv2.rectangle(rawImage,(leftX,leftY),(leftX+leftW,leftY+leftH),(222,255,155),2)
    cv2.rectangle(rawImage,(rightX,rightY),(rightX+rightW,rightX+rightH),(222,255,155),2)
    
#Render the image
cv2.imshow("Mask", mask)
cv2.imshow("raw",rawImage)
cv2.waitKey(0)
cv2.destroyAllWindows()

