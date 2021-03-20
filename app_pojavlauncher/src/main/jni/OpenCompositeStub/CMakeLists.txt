
get_filename_component(app_root ${CMAKE_SOURCE_DIR}/../../../.. ABSOLUTE)
#set(xr_sdk ${app_root}/libs/openxr-sdk)
add_subdirectory(${app_root}/libs/OpenComposite OpenComposite)

add_library(openvr_api SHARED
        OpenCompositeStub/oc_stub.cpp
        )

target_include_directories(openvr_api PUBLIC OpenCompositeStub)

# Link to OpenComposite
# Note we need Vulkan for the early pre-game-submits-frame state
target_link_libraries(openvr_api PRIVATE OCOVR vulkan GLESv3)

# Link to the android stuff
target_link_libraries(openvr_api PRIVATE android log)

# Rename OpenComposite's InitInternal so we can intercept those calls
# Also rename the original InitInternal otherwise it'll helpfully call the original one too
target_compile_options(OCOVR PRIVATE -DVR_InitInternal2=VR_InitInternal2_orig -DVR_InitInternal=VR_InitInternal_orig)

# Link to Oculus's OpenXR loader
set(oculus_openxr ${app_root}/libs/OculusOpenXR/OpenXR)
target_link_libraries(openvr_api PUBLIC ${oculus_openxr}/Libs/Android/${ANDROID_ABI}/Release/libopenxr_loader.so)
