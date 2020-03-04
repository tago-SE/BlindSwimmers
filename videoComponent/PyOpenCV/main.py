import cv2
import numpy as np

import find_roi as find_roi
from find_roi import RegionOfInterest

END_KEY = 'q'
LOWER_RED = np.array([161, 155, 84])
UPPRED_RED = np.array([179, 255, 255])


drawing = True
point1 = ()
point2 = ()

def mouse_drawing(event, x, y, flags, params):
    global point1, point2, drawing
    if event == cv2.EVENT_MOUSEMOVE:
        point1 = (x, y)
        point2 = (x + 20, y + 20)

if __name__ == "__main__":

    cap = cv2.VideoCapture('videos/MOV_0750.mp4')

    while True:
        
        _, frame = cap.read()

        frame = cv2.resize(frame, (960, 540))
        roi = find_roi.find_region_of_interest(frame)

        mouseX = 1000
        mouseY = 255

        cv2.setMouseCallback('frame', mouse_drawing)

        if point1 and point2:
            cv2.rectangle(frame, point1, point2, (0, 255, 0))

        if roi.is_inside(mouseX, mouseY, mouseX + 10, mouseY + 10):
            roi.draw(frame, drawContours=True, color=(0, 255, 0))
        else: 
            roi.draw(frame, drawContours=True, color=(0, 0, 255))
        
        cv2.imshow('frame', frame)  

        if cv2.waitKey(5) == ord(END_KEY):
            break

    
    cap.release()
    cv2.destroyAllWindows()