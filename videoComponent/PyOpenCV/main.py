import cv2
import numpy as np

import find_roi as find_roi
from find_roi import RegionOfInterest

END_KEY = 'q'


lower_red = np.array([161, 155, 84])
upper_red = np.array([179, 255, 255])

if __name__ == "__main__":

    cap = cv2.VideoCapture('videos/MOV_0750.mp4')

    while True:
        
        _, frame = cap.read()

        

        frame = cv2.resize(frame, (960, 540))

        roi = find_roi.find_region_of_interest(frame)
        roi.draw(frame, drawContours=False, color=(0, 255, 0))
        
        cv2.imshow('frame', frame)  

        if cv2.waitKey(5) == ord(END_KEY):
            break

    
    cap.release()
    cv2.destroyAllWindows()