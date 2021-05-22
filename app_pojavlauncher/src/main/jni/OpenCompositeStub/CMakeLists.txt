
get_filename_component(app_root ${CMAKE_SOURCE_DIR}/../../../.. ABSOLUTE)
#set(xr_sdk ${app_root}/libs/openxr-sdk)

#add_subdirectory(${app_root}/libs/OpenComposite OpenComposite)
add_subdirectory(/mnt/ssd/znix/static/programming/android/xrtest/OpenComposite OpenComposite)

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

# Ensure all the OpenVR ABI functions are exported
set(exported_funcs
        VR_GetGenericInterface
        VR_GetInitToken
        VR_GetStringForHmdError
        VR_GetVRInitErrorAsEnglishDescription
        VR_GetVRInitErrorAsSymbol
        VR_InitInternal
        VR_InitInternal2
        VR_IsHmdPresent
        VR_IsInterfaceVersionValid
        VR_IsRuntimeInstalled
        VR_RuntimePath
        VR_ShutdownInternal
        VRClientCoreFactory
        )
foreach (func_name IN LISTS exported_funcs)
    target_link_options(openvr_api PRIVATE -Wl,--require-defined=${func_name})
endforeach ()

# Link to Oculus's OpenXR loader
set(oculus_openxr ${app_root}/libs/OculusOpenXR/OpenXR)
target_link_libraries(openvr_api PUBLIC ${oculus_openxr}/Libs/Android/${ANDROID_ABI}/Release/libopenxr_loader.so)
