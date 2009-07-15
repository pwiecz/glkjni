LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := model
LOCAL_SRC_FILES := model.c
LOCAL_CFLAGS := -I$(LOCAL_PATH)/../glkjni -DANDROID
LOCAL_STATIC_LIBRARIES := glkjni
LOCAL_LDLIBS    := -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)

