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
if len(contours) != 0:
    cv2.drawContours(dilated,contours,-1,255)
    
#Render the image
cv2.imshow("Mask", mask)
cv2.waitKey(0)
cv2.destroyAllWindows()

