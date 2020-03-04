import cv2
import numpy as np

"""
    Creates two separate images for each contour and uses the AND operation on them to find any points that have a positive value. 
    These points will then be identified as intersections. 
"""
def contourIntersect(frame, contour1, contour2):
    # Two separate contours trying to check intersection on
    contours = [contour1, contour2]

    # Create image filled with zeros the same size of original image
    blank = np.zeros(frame.shape[0:2])

    # Copy each contour into its own image and fill it with '1'
    frame1 = cv2.drawContours(blank.copy(), contours, 0, 1)
    frame2 = cv2.drawContours(blank.copy(), contours, 1, 1)

    # Use the logical AND operation on the two images
    # Since the two images had bitwise and applied to it,
    # there should be a '1' or 'True' where there was intersection
    # and a '0' or 'False' where it didnt intersect
    intersection = np.logical_and(frame1, frame2)

    # Check if there was a '1' in the intersection
    return intersection.any()