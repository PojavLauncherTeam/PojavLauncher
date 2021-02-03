[![Android CI](https://github.com/PojavLauncherTeam/PojavLauncher/workflows/Android%20CI/badge.svg)](https://github.com/PojavLauncherTeam/PojavLauncher/actions)
[![Crowdin](https://badges.crowdin.net/pojavlauncher/localized.svg)](https://crowdin.com/project/pojavlauncher)
[![Discord](https://img.shields.io/discord/724163890803638273.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/6RpEJda)
[![Reddit](https://img.shields.io/badge/dynamic/json.svg?label=r/PojavLauncher%20member%20count&query=$.data.subscribers&url=https://www.reddit.com/r/PojavLauncher/about.json)](https://reddit.com/r/PojavLauncher)
# PojavLauncher

## Note
We do not exist on TikTok. No one from the dev team makes TikTok videos.

## Navigation
- [Introduction](#introduction)
- [Building](#building)
- [Current status](#current-status)
- [License](#license)
- [Contributing](#contributing)
- [Credits & Third party components and their licenses](#credits--third-party-components-and-their-licenses)

## Introduction
PojavLauncher is a Minecraft: Java Edition launcher for Android based on [Boardwalk](https://github.com/zhuowei/Boardwalk). This launcher can launch almost all available Minecraft versions (from rd-132211 to latest 1.17 snapshot, including Combat Test versions). Modding via Forge and Fabric are also supported.

## Building
To get started, you can just get prebuilt app from [stable release](https://github.com/PojavLauncherTeam/PojavLauncher/releases) or [automatic builds](https://github.com/PojavLauncherTeam/PojavLauncher/actions). If you want to build after launcher code changes, follow steps below.

Will be moved to **BUILDING.md**
### Java Runtime Environment (JRE)
- JRE for Android is [here](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u), also the build script [here](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch).
- Follow build instruction on build script [README.md](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/blob/buildjre8/README.md).
- You can also get [CI auto builds](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/actions).
- Spliting JRE and put to the launcher: **coming soon**.

### LWJGL and GLFW
- **Coming soon**

### The Launcher
- Because languages are auto added by Crowdin, so need to run language list generator before building. In this directory, run:
```
# On Linux, Mac OS:
chmod +x scripts/languagelist_updater.sh
bash scripts/languagelist_updater.sh

# On Windows:
scripts\languagelist_updater.bat
```
- Then, build use Android Studio.

## Current status
- [x] ~~OpenJDK 9 Mobile port: ARM32, ARM64, x86, x86_64.~~ Replaced by JRE8.
- [x] OpenJDK 8 Mobile port: ARM32, ARM64, x86, x86_64
- [x] Mod installer headless
- [x] Mod installer with GUI. Used `Caciocavallo` project for AWT without X11.
- [x] OpenGL in OpenJDK environment
- [x] OpenAL (work on most devices)
- [x] Support Minecraft 1.12.2 and below. Used [lwjglx](https://github.com/PojavLauncherTeam/lwjglx), a LWJGL2 compatibility layer for LWJGL3.
- [x] Support Minecraft 1.13 and above. Used [GLFW stub](https://github.com/PojavLauncherTeam/lwjgl3-glfw-java).
- [x] Game surface zooming.
- [x] New input pipe rewritten to native code to boost performance.
- [ ] More...

## Known Issues
- in 1.16 and up spawn eggs banners are white (you can fix this by adding this to your jvm flags 
``-Dorg.lwjgl.opengl.libname=libgl4es_115.so`` do this only works on 1.16 and up)
- controller mods arnt working
- with big modpacks texture could be mest up
- if your using gl4es 1.1.5 on 1.16 and lower texture will bug out when hit a mob
- probely more thats why we have a bug tracker ;)

## License
- PojavLauncher is licensed under [GNU GPLv3](https://github.com/khanhduytran0/PojavLauncher/blob/master/LICENSE).

## Contributing
Contributions are welcome! We welcome any type of contribution, not only code. Any code change should be submitted as a pull request. The description should explain what the code does and give steps to execute it.

## Credits & Third party components and their licenses
- [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
- Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- [GL4ES](https://github.com/ptitSeb/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).<br>
- [OpenJDK](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).<br>
- [LWJGL3](https://github.com/PojavLauncherTeam/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
- [LWJGLX](https://github.com/PojavLauncherTeam/lwjglx) (LWJGL2 API compatibility layer for LWJGL3): unknown license.<br>
- [pro-grade](https://github.com/pro-grade/pro-grade) (Java sandboxing security manager): [Apache License 2.0](https://github.com/pro-grade/pro-grade/blob/master/LICENSE.txt).

