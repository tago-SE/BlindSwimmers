# Requirements
* Python 3.6.8 (tested)
* Dependencies: Shapely, numpy, imutils, opencv-python

# Setup
    
    git clone https://github.com/tago-SE/BlindSwimmers.git
    pip install -r requirements.txt

# Commands

Run the algorithm on a video:

    py main.py -v myvideo.mp4

Run the algorithm with a live feed:

    py main.py

Arguments:

    -v <videofile.mp4>      # run videofile
    -r <angle>              # rotate frame
    -w <width>              # frame width
    -h <height>             # frame height 

Demo:
https://www.youtube.com/watch?v=rmICLOLq2Ro&t=4s
