LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_CAMERA_MODULES:=off
#OPENCV_INSTALL_MODULES:=off
#OPENCV_LIB_TYPE:=SHARED
OPENCV_INSTALL_MODULES:=on

  include F:\Dev\OpenCV-3.1.0-android-sdk\OpenCV-android-sdk\sdk\native\jni\OpenCV.mk


#LOCAL_SRC_FILES  := DetectionBasedTracker_jni.cpp
#LOCAL_C_INCLUDES += $(LOCAL_PATH)
#LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := Mat

include $(BUILD_SHARED_LIBRARY)
  