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

last_entered_ts = 0
last_exited_ts = 0
last_found_ts = 0
has_left = True 
pool_length = 25

# Time in seconds which the object is allowed to be lost without alerting the system on exit
MIN_TIME_SINCE_LAST_FOUND = 1.0 

lapses = []

def __on_enter(ts):
    global last_entered_ts 
    last_entered_ts = ts
    print("ENTER: ", ts) 


def __on_exit(ts):
    global has_left, last_exited_ts

    if (last_exited_ts != 0):
        lapse_time = t - last_exited_ts
        print("lapse duration: ", lapse_time, " s") 
        velocity = pool_length/lapse_time
        print("average swim speed: ", velocity, " m/s")
        lapses.append(lapse_time)       
    last_exited_ts = t - MIN_TIME_SINCE_LAST_FOUND
    print("EXIT: ", last_exited_ts)
    has_left = True


if __name__ == "__main__":

    cap = cv2.VideoCapture('videos/MOV_0747.mp4')

    while cap.isOpened():
        
        _, frame = cap.read()

        frame = cv2.resize(frame, (960, 540))

        #cv2.setMouseCallback('frame', mouse_drawing)
        #(mx, my) = mousePos
        #cv2.rectangle(frame, (mx, my), (mx + 10, my + 10), (0, 255, 0))

        roi = find_roi.find_region_of_interest(frame)
        swimmer = find_swimmer.find_swimmer(frame, roi)

        t = time.time()
        if swimmer != None: 
            last_found_ts = t
            if (last_entered_ts < t - MIN_TIME_SINCE_LAST_FOUND and has_left):
                __on_enter(t)
            
            has_left = False 
            # Render 
            swimmer.draw(frame, (0, 0, 0), 2)
      
        else: 
            # if the object hasn't been found for a certain amount of time, and it was previously inside the ROI
            if last_found_ts < t - MIN_TIME_SINCE_LAST_FOUND and not has_left: 
                __on_exit(t)
    
        if has_left:
            roi.draw(frame, drawLaneContours=False, color=(0, 0, 255))
        else: 
            roi.draw(frame, drawLaneContours=False, color=(0, 255, 0))

        cv2.imshow('frame', frame)  

        if cv2.waitKey(5) == ord(END_KEY):
            break

    cap.release()
    cv2.destroyAllWindows()
