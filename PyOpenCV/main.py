import cv2
import numpy as np
import time 
import sys 
import imutils
import args_helper as args_helper
import hsv_picker as hsv_picker

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

DEFAULT_FRAME_H = 540
DEFAULT_FRAME_W = 960


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

    videofile = 0 # default, watch live feed
    
    arg_len = len(sys.argv)
    if arg_len > 1:
        if sys.argv[1] == "-v" or sys.argv[1] == "-video": 
            if arg_len <= 2:
                print("ERROR: Need to specify video file.") 
                exit()  
            videofile = sys.argv[2]
            if not (videofile.endswith(".mp4")):
                print("ERROR: Videofile must end with .mp4")
                exit()  

    rotation_angle = 0
    if args_helper.is_key_present(sys.argv, "-r") or args_helper.is_key_present(sys.argv, "-rotation"):
        rotation_angle = int(args_helper.get_value_after_key(sys.argv, "-r", "-rotation"))


    frameW = DEFAULT_FRAME_W
    if args_helper.is_key_present(sys.argv, "-w") or args_helper.is_key_present(sys.argv, "-width"):
        frameW = int(args_helper.get_value_after_key(sys.argv, "-w", "-width"))
    frameH = DEFAULT_FRAME_H
    if args_helper.is_key_present(sys.argv, "-h") or args_helper.is_key_present(sys.argv, "-height"):
        frameH = int(args_helper.get_value_after_key(sys.argv, "-h", "-height"))


    cap = cv2.VideoCapture(videofile)
    
    i = -1
    while cap.isOpened():
        _, frame = cap.read()

        if rotation_angle != 0:
            frame = imutils.rotate_bound(frame, angle=rotation_angle)
        
        i = (i + 1) % FRAME_PROCESSED_FACTOR
        if not i == 0:
            continue
            
        frame = cv2.resize(frame, (frameW, frameH))
        cv2.imshow('frame', frame)  

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
    
        if roi != None: 
            if has_left:
                roi.draw(frame, drawLaneContours=False, color=(0, 0, 255))
            else: 
                roi.draw(frame, drawLaneContours=False, color=(0, 255, 0))

        cv2.imshow('frame', frame)  

        swimmer_mask = find_swimmer.get_swimmer_mask()
        cv2.imshow('mask', swimmer_mask)  
     

        key = cv2.waitKey(5)
        if key == ord(END_KEY):
            break
    
        elif key == ord("p"):
            hsv_picker.set_image(frame)
            hsv_picker.setup_trackbars("HSV")

            cv2.waitKey(0) 
        
        elif key == ord("b"):
            cv2.waitKey(0) 

    cap.release()
    cv2.destroyAllWindows()
