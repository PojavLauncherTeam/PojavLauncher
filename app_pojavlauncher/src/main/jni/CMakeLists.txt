cmake_minimum_required(VERSION 3.10.2)
project("pojavlauncher")

add_library(pojavexec SHARED
        egl_bridge.c
        input_bridge_v3.c
        jre_launcher.c
        utils.c
        )
target_link_libraries(pojavexec PRIVATE -ldl -llog -landroid -lEGL)

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
