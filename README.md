[![Android CI](https://github.com/PojavLauncherTeam/PojavLauncher/workflows/Android%20CI/badge.svg)](https://github.com/PojavLauncherTeam/PojavLauncher/actions)
# PojavLauncher
A Minecraft: Java Edition launcher for Android based from Boardwalk. This branch is intended to support 1.13+

## This branch?
- Attempt to run Minecraft in OpenJDK, similar way with Boardwalk 2.

## About OpenJDK 9 Java Runtime Mobile
Build script: [PojavLauncherTeam/android-openjdk-build-multiarch](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch)<br>
Source code: http://hg.openjdk.java.net/mobile/jdk9 <br>
Architectures:
- For ARMv7 (aarch32): **working**.
- For ARM64 (aarch64): **working**.
- For x86 (i\*86): **working**.
- For x86_64 (amd64): **working**.

## Current status
- [x] **Removed** ~~BinaryExecutor: execute `java` binary, no `JNIInvocation`.~~
- [x] **Temporary removed** ~~JVDroid OpenJDK 11 (32 and 64-bit ARM and x86). Partial, no error `can't lock mutex`, but now exit with none output.~~
- [x] OpenJDK 9 Mobile port: ARM32, ARM64, x86, x86_64.
- [x] Mod installer headless
- [ ] Mod installer with GUI. Will try own graphics environment~~use `Caciocavallo` project~~.
- [x] OpenGL in OpenJDK environment
- [x] OpenAL (work on most devices)
- [x] Input keyboard and mouse events from ART to OpenJDK environment
- [x] Support Minecraft 1.12.2 and below. Used [lwjglx](https://github.com/PojavLauncherTeam/lwjglx), a LWJGL2 compatibility layer for LWJGL3.
- [x] Support Minecraft 1.13 and above. Used [GLFW stub](https://github.com/PojavLauncherTeam/lwjgl3-glfw-java).
- [ ] More...

## License
- PojavLauncher is licensed under GPLv3.

