
find_path(appglue_dir android_native_app_glue.h PATHS ${ANDROID_NDK}/sources/android/native_app_glue)

add_library(vr_launcher SHARED
        VrLauncher/vrl_main.cpp
        VrLauncher/log.cpp

        #VrLauncher/sample_app.cpp

        VrLauncher/jlaunch.cpp
        VrLauncher/start_java.cpp

        ${appglue_dir}/android_native_app_glue.c
        )

target_include_directories(vr_launcher PRIVATE ${appglue_dir})

target_link_libraries(vr_launcher PRIVATE openvr_api OpenXR EGL GLESv3 android log)

# TESTING!
#target_link_libraries(vr_launcher PRIVATE OCOVR vulkan GLESv3 android log EGL)
#set(vr_launcher ${app_root}/libs/OculusOpenXR/OpenXR)
#target_link_libraries(vr_launcher PRIVATE ${oculus_openxr}/Libs/Android/${ANDROID_ABI}/Release/libopenxr_loader.so)
