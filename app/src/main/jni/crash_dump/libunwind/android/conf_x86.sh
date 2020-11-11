#!/bin/bash

if [[ "$OUT" == "" ]]; then
  echo "In order for this script to function, please choose an arm target"
  echo "using source build/envsetup.sh and lunch XXX\n"
  exit 1
fi

cc="${ANDROID_TOOLCHAIN}/i686-linux-android-gcc"
cpp="${ANDROID_TOOLCHAIN}/i686-linux-android-g++"

includes=(
  "-isystem ${ANDROID_BUILD_TOP}/system/core/include"
  "-isystem ${ANDROID_BUILD_TOP}/hardware/libhardware/include"
  "-isystem ${ANDROID_BUILD_TOP}/hardware/libhardware_legacy/include"
  "-isystem ${ANDROID_BUILD_TOP}/hardware/ril/include"
  "-isystem ${ANDROID_BUILD_TOP}/libnativehelper/include"
  "-isystem ${ANDROID_BUILD_TOP}/frameworks/native/include"
  "-isystem ${ANDROID_BUILD_TOP}/frameworks/native/opengl/include"
  "-isystem ${ANDROID_BUILD_TOP}/frameworks/av/include"
  "-isystem ${ANDROID_BUILD_TOP}/frameworks/base/include"
  "-isystem ${ANDROID_BUILD_TOP}/external/skia/include"
  "-isystem ${OUT}/obj/include"
  "-isystem ${ANDROID_BUILD_TOP}/bionic/libc/arch-x86/include"
  "-isystem ${ANDROID_BUILD_TOP}/bionic/libc/include"
  "-isystem ${ANDROID_BUILD_TOP}/bionic/libstdc++/include"
  "-isystem ${ANDROID_BUILD_TOP}/bionic/libc/kernel/uapi"
  "-isystem ${ANDROID_BUILD_TOP}/bionic/libc/kernel/uapi/asm-x86"
  "-isystem ${ANDROID_BUILD_TOP}/bionic/libm/include"
  "-isystem ${ANDROID_BUILD_TOP}/bionic/libm/include/i387"
  "-isystem ${ANDROID_BUILD_TOP}/bionic/libthread_db/include"
)

ldflags=(
  "-m32"
  "-Wl,-z,noexecstack"
  "-Wl,-z,relro"
  "-Wl,-z,now"
  "-Wl,--warn-shared-textrel"
  "-Wl,--gc-sections"
  "-nostdlib"
  "-Bdynamic"
  "-Wl,-dynamic-linker,/system/bin/linker"
  "-Wl,-z,nocopyreloc"
  "-fPIE"
  "-pie"
  "-L${OUT}/obj/lib"
  "-Wl,-rpath-link=${OUT}/obj/lib"
  "-Wl,--whole-archive"
  "-Wl,--no-whole-archive"
  "-lc"
  "-lstdc++"
  "-lm"
  "-Wl,--no-undefined"
  "-ldl"
)

eval ./configure CC=\"${cc} ${includes[@]}\" \
                 CPP=\"${cc} ${includes[@]} -E\" \
                 CXX=\"${cpp} ${includes[@]}\" \
                 CXXCPP=\"${cpp} ${includes[@]} -E\" \
                 LDFLAGS=\"${ldflags[@]}\" \
                 --host=i386
