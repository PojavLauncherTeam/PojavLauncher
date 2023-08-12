#!/bin/bash
set -e

copy_libs() {
  echo "# Copying libraries for $1"

  # Extract natives
  mkdir -p linux/$1/org/lwjgl/{shaderc,vma}
  wget -q https://nightly.link/PojavLauncherTeam/lwjgl3/workflows/build-android/3.3.1/lwjgl3-android-natives-$1.zip
  unzip lwjgl3-android-natives-$1.zip libshaderc.so liblwjgl_vma.so; rm lwjgl3-android-natives-$1.zip
  mv libshaderc.so linux/$1/org/lwjgl/shaderc/
  mv liblwjgl_vma.so linux/$1/org/lwjgl/vma/

  # Overwrite natives
  zip -gr META-INF/jars/lwjgl-shaderc-3.3.1-natives-linux.jar linux/$1/org/lwjgl/shaderc
  zip -gr META-INF/jars/lwjgl-vma-3.3.1-natives-linux.jar linux/$1/org/lwjgl/vma

  # Cleanup
  rm -r linux
}

if [ -z "$1" ]  || [ -z "$2" ] ; then
  echo "Usage: $0 /path/to/VulkanMod.jar [architectures...]"
  echo "Valid architectures: arm64 arm32 x64 x86"
  exit 1
fi

export TMPDIR=$TMPDIR/vkmodpatch
rm -rf $TMPDIR; mkdir $TMPDIR; cd $TMPDIR
unzip $1 'META-INF/jars/lwjgl-*-3.3.1-natives-linux.jar' META-INF/jars/lwjgl-vulkan-3.3.1.jar

# Overwrite lwjgl-vulkan.jar
unzip META-INF/jars/lwjgl-vulkan-3.3.1.jar 'META-INF/*' fabric.mod.json -d lwjgl-vulkan
wget -q https://nightly.link/PojavLauncherTeam/lwjgl3/workflows/build-android/3.3.1/lwjgl3-android-modules.zip
unzip lwjgl3-android-modules.zip lwjgl-vulkan/lwjgl-vulkan.jar; rm lwjgl3-android-modules.zip
mv lwjgl-vulkan/lwjgl-vulkan.jar META-INF/jars/lwjgl-vulkan-3.3.1.jar
(cd lwjgl-vulkan && zip -r ../META-INF/jars/lwjgl-vulkan-3.3.1.jar META-INF fabric.mod.json)
rm -r lwjgl-vulkan

# Process every arch
for arg in "$@"; do
  if [ "$arg" != "$1" ]; then
    copy_libs $arg
  fi
done

# Package everything back
zip -gr $1 META-INF

# Cleanup
rm -rf $TMPDIR

echo "Done"
