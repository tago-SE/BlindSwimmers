import cv2
import numpy as np
import time 
import sys 

import find_roi as find_roi
from find_roi import RegionOfInterest
import find_swimmer
from find_swimmer import Swimmer

END_KEY = 'q'

drawing = True
mousePos = (0, 0)

last_entered_ts = 0
last_exited_ts = 0
last_found_ts = 0
has_left = True 
pool_length = 25

# Default value is 1 which means that every frame will be processed. 2 = every other frame, 3 = every third frame and so on...
FRAME_PROCESSED_FACTOR = 1
if FRAME_PROCESSED_FACTOR < 1:
    FRAME_PROCESSED_FACTOR = 1

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

    arg_len = len(sys.argv)
    if arg_len <= 1:
        print("ERROR: Need to provide arguments")
        exit()
    if sys.argv[1] == "-v" or sys.argv[1] == "-video": 
        if arg_len <= 2:
            print("ERROR: Need to specify video file.") 
            exit()  
        videofile = sys.argv[2]
        if not (videofile.endswith(".mp4")):
            print("ERROR: Videofile must end with .mp4")
            exit()
    elif sys.argv[1] == "-c" or sys.argv[1] == "-camera":
        videofile = 0
        print("WARNING: Live camera feed has not been tested properly.")    
    else:
        print("ERROR: Command was not recognized.")
        exit()

    cap = cv2.VideoCapture(videofile)
    

    i = -1
    while cap.isOpened():
        _, frame = cap.read()
        i = (i + 1) % FRAME_PROCESSED_FACTOR
        if not i == 0:
            continue
            
        frame = cv2.resize(frame, (960, 540))

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
