# Requirements
* Python 3.6.8 (tested)
* Dependencies: Shapely, numpy, opencv-python

# Setup
    
    git clone https://github.com/tago-SE/BlindSwimmers.git
    pip install -r requirements.txt

# Commands

Run the algorithm on a video:

    py main.py -v myvideo.mp4

Run the algorithm with a live feed:

    py main.py -c 

Arguments:
    -v <videofile.mp4>      # apply on videofile
    -c                      # apply on live camera feed
    -r <angle>              # rotate