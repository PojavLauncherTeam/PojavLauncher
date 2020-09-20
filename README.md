![Android CI](https://github.com/PojavLauncherTeam/PojavLauncher/workflows/Android%20CI/badge.svg)
# PojavLauncher
A Minecraft: Java Edition launcher for Android based from Boardwalk. This branch is intended to support 1.13+

## This branch?
- Attempt to run Minecraft in OpenJDK, different a bit with Boardwalk 2.

## Current status
- [x] **Removed** ~~BinaryExecutor: execute `java` binary, no `JNIInvocation`.~~
- [x] **Temporary removed** ~~JVDroid OpenJDK 11 (32 and 64-bit ARM and x86). Partial, no error `can't lock mutex`, but now exit with none output.~~
- [x] OpenJDK 9 Mobile port
- [ ] AWT/Swing for mod installer.Will try own graphics environment~~use `Caciocavallo` project~~.
- [x] OpenGL in OpenJDK environment
- [ ] OpenAL
- [x] Input keyboard and mouse events from ART to OpenJDK environment
- [ ] Support Minecraft 1.12 and below. Used GLFW stub.
- [x] Support Minecraft 1.13 and above. Will make a wrapper LWJGL2 to 3.
- [ ] More...

## License
- PojavLauncher is licensed under GPLv3.

