import numpy as np
import cv2

# Load an color image in grayscale
img = cv2.imread('Swimming-Technique.png')

cv2.imshow('original image',img)

HSV_img = cv2.cvtColor(img,cv2.COLOR_BGR2HSV)
   # Convert BGR to HSV
cv2.imshow('HSV format image', HSV_img)

    # define range of blue color in HSV
lower_blue = np.array([110,50,50])
upper_blue = np.array([130,255,255])

# Threshold the HSV image to get only blue colors
mask = cv2.inRange(img, lower_blue, upper_blue)

# Bitwise-AND mask and original image
res = cv2.bitwise_and(img,img, mask= mask)

cv2.imshow('Blue-pixels', res)
#cv2.imshow('image',img)
cv2.waitKey(0)
cv2.destroyAllWindows()