cmake_minimum_required(VERSION 3.10.2)
project("pojavlauncher")

add_library(pojavexec SHARED
        egl_bridge.c
        input_bridge_v3.c
        jre_launcher.c
        utils.c
        )
# gles for the debug string stuff
target_link_libraries(pojavexec PRIVATE -ldl -llog -landroid -lEGL -lGLESv3)

add_library(istdio SHARED
        stdio_is.c
        )

add_library(pojavexec_awt SHARED
        awt_bridge.c
        )

# # FIXME shouldn't actually be built, just used to make awt_xawt depend on awt_headless
# add_library(awt_headless SHARED
#         empty.c
#         )

add_library(awt_xawt SHARED
        empty.c
        # xawt_fake.c # doesn't exist?
        )
# target_link_libraries(awt_xawt PRIVATE awt_headless) # FIXME

set(gl4es_dir /mnt/ssd/znix/static/programming/android/gl4es)
include(${gl4es_dir}/android.cmake)

include(OpenCompositeStub/CMakeLists.txt)
include(VrLauncher/CMakeLists.txt)

# Required so it can send the EGL info to OC
target_link_libraries(pojavexec PRIVATE openvr_api)
