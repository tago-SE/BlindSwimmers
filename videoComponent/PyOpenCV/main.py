import cv2
import numpy as np
import time 

import find_roi as find_roi
from find_roi import RegionOfInterest
import find_swimmer
from find_swimmer import Swimmer

END_KEY = 'q'

drawing = True
mousePos = (0, 0)

def mouse_drawing(event, x, y, flags, params):
    global mousePos
    if event == cv2.EVENT_MOUSEMOVE:
        mousePos = (x, y)

last_inside_ts = 0
last_found_ts = 0
has_left = True 

if __name__ == "__main__":

    cap = cv2.VideoCapture('videos/MOV_0750.mp4')

    while cap.isOpened():
        
        _, frame = cap.read()

        frame = cv2.resize(frame, (960, 540))

        cv2.setMouseCallback('frame', mouse_drawing)
        (mx, my) = mousePos
        cv2.rectangle(frame, (mx, my), (mx + 10, my + 10), (0, 255, 0))

        roi = find_roi.find_region_of_interest(frame)

        swimmer_was_inside = False 
        swimmer = find_swimmer.find_swimmer(frame, roi)

        t = time.time()
        if swimmer != None: 
            if roi.rect_is_inside(swimmer.x, swimmer.y, swimmer.x + swimmer.w, swimmer.y + swimmer.h):
                swimmer_was_inside = True 
        
        if (swimmer_was_inside):
            last_found_ts = t
            has_left = False 
            # Detect swimmer entering ROI 
            if (last_inside_ts < t - 15.0):
                print("ENTERED at: %d", t)
                last_inside_ts = t

            # Render 
            swimmer.draw(frame, (0, 0, 0), 2)
            roi.draw(frame, drawLaneContours=False, color=(0, 255, 0))
        else: 
            if last_found_ts < t - 3.0 and not has_left: 
                  print("LEFT at: %d", (t - 3.0))
                  has_left = True

            # Render 
            roi.draw(frame, drawLaneContours=False, color=(0, 0, 255))
    
        cv2.imshow('frame', frame)  

        if cv2.waitKey(5) == ord(END_KEY):
            break

    
    cap.release()
    cv2.destroyAllWindows()