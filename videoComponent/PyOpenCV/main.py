import cv2
import numpy as np

import find_roi as find_roi
from find_roi import RegionOfInterest
import find_swimmer
from find_swimmer import Swimmer

END_KEY = 'q'
LOWER_RED = np.array([161, 155, 84])
UPPRED_RED = np.array([179, 255, 255])


drawing = True
mousePos = (0, 0)

def mouse_drawing(event, x, y, flags, params):
    global mousePos
    if event == cv2.EVENT_MOUSEMOVE:
        mousePos = (x, y)

if __name__ == "__main__":

    cap = cv2.VideoCapture('videos/MOV_0744.mp4')

    while True:
        
        _, frame = cap.read()

        frame = cv2.resize(frame, (960, 540))

        cv2.setMouseCallback('frame', mouse_drawing)
        (mx, my) = mousePos
        cv2.rectangle(frame, (mx, my), (mx + 10, my + 10), (0, 255, 0))

        roi = find_roi.find_region_of_interest(frame)

        swimmer_was_inside = False 
        swimmer = find_swimmer.find_swimmer(frame, roi)
    
        if swimmer != None: 
            #swimmer.draw(frame)
            if roi.rect_is_inside(swimmer.x, swimmer.y, swimmer.x + swimmer.w, swimmer.y + swimmer.h):
                swimmer_was_inside = True 
        
        if (swimmer_was_inside):
            swimmer.draw(frame, (0, 0, 0), 2)
            roi.draw(frame, drawLaneContours=False, color=(0, 255, 0))
        else: 
            roi.draw(frame, drawLaneContours=False, color=(0, 0, 255))
    
        cv2.imshow('frame', frame)  

        if cv2.waitKey(5) == ord(END_KEY):
            break

    
    cap.release()
    cv2.destroyAllWindows()