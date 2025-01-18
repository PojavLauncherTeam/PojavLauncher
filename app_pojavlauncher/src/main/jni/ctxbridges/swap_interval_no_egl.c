//
// Created by maks on 08.11.2023.
//

#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <android/log.h>
#include <android/native_window.h>

// Taken from https://android.googlesource.com/platform/frameworks/native/+/41abd67/include/ui/egl/android_natives.h
// Might be outdated, if you can find a more recent version please add it there
// region android_native_base_t definition
typedef struct android_native_base_t
{
    /* a magic value defined by the actual EGL native type */
    int magic;
    /* the sizeof() of the actual EGL native type */
    int version;
    void* reserved[4];
    /* reference-counting interface */
    void (*incRef)(struct android_native_base_t* base);
    void (*decRef)(struct android_native_base_t* base);
} android_native_base_t;
// endregion
// region ANativeWindow magic definition
#define ANDROID_NATIVE_MAKE_CONSTANT(a,b,c,d) \
    (((unsigned)(a)<<24)|((unsigned)(b)<<16)|((unsigned)(c)<<8)|(unsigned)(d))
#define ANDROID_NATIVE_WINDOW_MAGIC \
    ANDROID_NATIVE_MAKE_CONSTANT('_','w','n','d')
// endregion
struct ANativeWindowBuffer; // opaque, actually an internal system struct but we don't use it

// Taken from https://android.googlesource.com/platform/frameworks/native/+/refs/heads/main/libs/nativewindow/include/system/window.h
// region ANativeWindow struct definition
struct ANativeWindow_real
{
    struct android_native_base_t common;
    /* flags describing some attributes of this surface or its updater */
    const uint32_t flags;
    /* min swap interval supported by this updated */
    const int   minSwapInterval;
    /* max swap interval supported by this updated */
    const int   maxSwapInterval;
    /* horizontal and vertical resolution in DPI */
    const float xdpi;
    const float ydpi;
    /* Some storage reserved for the OEM's driver. */
    intptr_t    oem[4];
    /*
     * Set the swap interval for this surface.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*setSwapInterval)(struct ANativeWindow* window,
                               int interval);
    /*
     * Hook called by EGL to acquire a buffer. After this call, the buffer
     * is not locked, so its content cannot be modified. This call may block if
     * no buffers are available.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     *
     * Returns 0 on success or -errno on error.
     *
     * XXX: This function is deprecated.  It will continue to work for some
     * time for binary compatibility, but the new dequeueBuffer function that
     * outputs a fence file descriptor should be used in its place.
     */
    int     (*dequeueBuffer_DEPRECATED)(struct ANativeWindow* window,
                                        struct ANativeWindowBuffer** buffer);
    /*
     * hook called by EGL to lock a buffer. This MUST be called before modifying
     * the content of a buffer. The buffer must have been acquired with
     * dequeueBuffer first.
     *
     * Returns 0 on success or -errno on error.
     *
     * XXX: This function is deprecated.  It will continue to work for some
     * time for binary compatibility, but it is essentially a no-op, and calls
     * to it should be removed.
     */
    int     (*lockBuffer_DEPRECATED)(struct ANativeWindow* window,
                                     struct ANativeWindowBuffer* buffer);
    /*
     * Hook called by EGL when modifications to the render buffer are done.
     * This unlocks and post the buffer.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     *
     * Buffers MUST be queued in the same order than they were dequeued.
     *
     * Returns 0 on success or -errno on error.
     *
     * XXX: This function is deprecated.  It will continue to work for some
     * time for binary compatibility, but the new queueBuffer function that
     * takes a fence file descriptor should be used in its place (pass a value
     * of -1 for the fence file descriptor if there is no valid one to pass).
     */
    int     (*queueBuffer_DEPRECATED)(struct ANativeWindow* window,
                                      struct ANativeWindowBuffer* buffer);
    /*
     * hook used to retrieve information about the native window.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*query)(const struct ANativeWindow* window,
                     int what, int* value);
    /*
     * hook used to perform various operations on the surface.
     * (*perform)() is a generic mechanism to add functionality to
     * ANativeWindow while keeping backward binary compatibility.
     *
     * DO NOT CALL THIS HOOK DIRECTLY.  Instead, use the helper functions
     * defined below.
     *
     * (*perform)() returns -ENOENT if the 'what' parameter is not supported
     * by the surface's implementation.
     *
     * See above for a list of valid operations, such as
     * NATIVE_WINDOW_SET_USAGE or NATIVE_WINDOW_CONNECT
     */
    int     (*perform)(struct ANativeWindow* window,
                       int operation, ... );
    /*
     * Hook used to cancel a buffer that has been dequeued.
     * No synchronization is performed between dequeue() and cancel(), so
     * either external synchronization is needed, or these functions must be
     * called from the same thread.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     *
     * XXX: This function is deprecated.  It will continue to work for some
     * time for binary compatibility, but the new cancelBuffer function that
     * takes a fence file descriptor should be used in its place (pass a value
     * of -1 for the fence file descriptor if there is no valid one to pass).
     */
    int     (*cancelBuffer_DEPRECATED)(struct ANativeWindow* window,
                                       struct ANativeWindowBuffer* buffer);
    /*
     * Hook called by EGL to acquire a buffer. This call may block if no
     * buffers are available.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     *
     * The libsync fence file descriptor returned in the int pointed to by the
     * fenceFd argument will refer to the fence that must signal before the
     * dequeued buffer may be written to.  A value of -1 indicates that the
     * caller may access the buffer immediately without waiting on a fence.  If
     * a valid file descriptor is returned (i.e. any value except -1) then the
     * caller is responsible for closing the file descriptor.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*dequeueBuffer)(struct ANativeWindow* window,
                             struct ANativeWindowBuffer** buffer, int* fenceFd);
    /*
     * Hook called by EGL when modifications to the render buffer are done.
     * This unlocks and post the buffer.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     *
     * The fenceFd argument specifies a libsync fence file descriptor for a
     * fence that must signal before the buffer can be accessed.  If the buffer
     * can be accessed immediately then a value of -1 should be used.  The
     * caller must not use the file descriptor after it is passed to
     * queueBuffer, and the ANativeWindow implementation is responsible for
     * closing it.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*queueBuffer)(struct ANativeWindow* window,
                           struct ANativeWindowBuffer* buffer, int fenceFd);
    /*
     * Hook used to cancel a buffer that has been dequeued.
     * No synchronization is performed between dequeue() and cancel(), so
     * either external synchronization is needed, or these functions must be
     * called from the same thread.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     *
     * The fenceFd argument specifies a libsync fence file decsriptor for a
     * fence that must signal before the buffer can be accessed.  If the buffer
     * can be accessed immediately then a value of -1 should be used.
     *
     * Note that if the client has not waited on the fence that was returned
     * from dequeueBuffer, that same fence should be passed to cancelBuffer to
     * ensure that future uses of the buffer are preceded by a wait on that
     * fence.  The caller must not use the file descriptor after it is passed
     * to cancelBuffer, and the ANativeWindow implementation is responsible for
     * closing it.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*cancelBuffer)(struct ANativeWindow* window,
                            struct ANativeWindowBuffer* buffer, int fenceFd);
};
// endregion

void setNativeWindowSwapInterval(struct ANativeWindow* nativeWindow, int swapInterval) {
    if(!getenv("POJAV_VSYNC_IN_ZINK")) {
        return;
    }
    struct ANativeWindow_real* nativeWindowReal = (struct ANativeWindow_real*) nativeWindow;
    if(nativeWindowReal->common.magic != ANDROID_NATIVE_WINDOW_MAGIC) {
        __android_log_print(ANDROID_LOG_WARN, "SwapIntervalNoEGL", "ANativeWindow magic does not match. Expected %i, got %i",
                            ANDROID_NATIVE_WINDOW_MAGIC, nativeWindowReal->common.magic);
        return;
    }
    if(nativeWindowReal->common.version != sizeof(struct ANativeWindow_real)) {
        __android_log_print(ANDROID_LOG_WARN, "SwapIntervalNoEGL", "ANativeWindow version does not match. Expected %i, got %i",
                            sizeof(struct ANativeWindow_real), nativeWindowReal->common.version);
        return;
    }
    int error;
    if((error = nativeWindowReal->setSwapInterval(nativeWindow, swapInterval)) != 0) {
        __android_log_print(ANDROID_LOG_WARN, "SwapIntervalNoEGL", "Failed to set swap interval: %s", strerror(-error));
    }
}