import numpy as np
import cv2
import imutils
import argparse

from imutils.video import FPS

ap = argparse.ArgumentParser()
ap.add_argument("-v", "--video", required=True,
    help="P1033658.mp4")
args = vars(ap.parse_args())
stream = cv2.VideoCapture(args["video"])
fps = FPS().start()
# timer starts that we can use to measure FPS, or more specifically, 
# the throughput rate of our video processing pipeline.
while(True):
    #read the frame
    (grabbed, frame) = stream.read() 
    #read  method is a blocking operation —
    if not grabbed:
        break
  #  gray = cv2.cvtColor(frame, cv2.COLOR_RGB2GRAY)
    #display the frame
    frame = imutils.resize(frame,width=450)
    frame = cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
    frame = np.dstack([frame,frame,frame])

    cv2.putText(frame, "Slow Method", (10, 30),
        cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)  
    cv2.imshow('frame',frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        # breaking the loop if the user types q
        # note that the video window must be highlighted!
        break
    fps.update()
fps.stop()
print("[INFO] elasped time: {:.2f}".format(fps.elapsed()))
print("[INFO] approx. FPS: {:.2f}".format(fps.fps()))
stream.release()
cv2.destroyAllWindows()
