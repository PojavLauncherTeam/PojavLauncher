
get_filename_component(app_root ${CMAKE_SOURCE_DIR}/../../../.. ABSOLUTE)
#set(xr_sdk ${app_root}/libs/openxr-sdk)
add_subdirectory(${app_root}/libs/OpenComposite OpenComposite)

add_library(openvr_api SHARED
        OpenCompositeStub/oc_stub.cpp
        )

# Link to OpenComposite
# Note we need Vulkan for the early pre-game-submits-frame state
target_link_libraries(openvr_api PRIVATE OCOVR vulkan GLESv3)

# Link to the android stuff
target_link_libraries(openvr_api PRIVATE android log)

# Force the linker to not eliminate the symbols we want
target_link_options(openvr_api PRIVATE -Wl,--require-defined -Wl,VR_InitInternal2)

# Link to Oculus's OpenXR loader
set(oculus_openxr ${app_root}/libs/OculusOpenXR/OpenXR)
target_link_libraries(openvr_api PRIVATE ${oculus_openxr}/Libs/Android/${ANDROID_ABI}/Release/libopenxr_loader.so)
