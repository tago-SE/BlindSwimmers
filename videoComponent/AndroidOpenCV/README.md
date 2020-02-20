Adding OpenCV module to android studio project: 
https://www.youtube.com/watch?v=pzuwrYgOnDQ

OpenCV Android download: 
https://opencv.org/releases/

1. Make sure you download a version that suits your android studio version...

2. new -> Import Module -> 
C:\Program Files (x86)\opencv-3.4.7-android-sdk\OpenCV-android-sdk\sdk\java


Change compileSdkVersion 17 to minimum 21 (recommended latest). Because android.hardware.camera2 was added in API 21.
https://stackoverflow.com/questions/36204781/error-package-android-hardware-camera2-does-not-exist-opencv



Problem:
OpenCV error: Cannot load info library for OpenCV 

Solution: 
File -> Project Structures -> Dependencies ... (See Indian Guy)

Make sure build.gradle (Module: app) and build.gradle (Module:openCV have the same compile and target version).


Create a JNI Folder in srcs/main... (See Indian Guy)
