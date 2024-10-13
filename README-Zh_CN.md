[![Android CI](https://github.com/PojavLauncherTeam/PojavLauncher/workflows/Android%20CI/badge.svg)](https://github.com/PojavLauncherTeam/PojavLauncher/actions)
[![Crowdin](https://badges.crowdin.net/pojavlauncher/localized.svg)](https://crowdin.com/project/pojavlauncher)
[![Discord](https://img.shields.io/discord/724163890803638273.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/6RpEJda)
[![Reddit](https://img.shields.io/badge/dynamic/json.svg?label=r/PojavLauncher%20member%20count&query=$.data.subscribers&url=https://www.reddit.com/r/PojavLauncher/about.json)](https://reddit.com/r/PojavLauncher) 
[![Google Play](https://gist.githubusercontent.com/meefik/54a54afa7cc1dc600bdb855cb7895a4a/raw/ad617c006a1ac28d067c9a87cec60199ca8fef7c/get-it-on-google-play.png)](https://play.google.com/store/apps/details?id=net.kdt.pojavlaunch)

# PojavLauncher 
README-en_US [here](https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/README.md)

## Note
- We do not exist on TikTok. No one from the dev team makes TikTok videos. 
- PojavLauncher官方推特账号 [@PLaunchTeam](https://twitter.com/PLaunchTeam). Any others (most notably @PojavLauncher) are fake, please report them to Twitter's moderation team.

## Navigation
- [Introduction](#introduction)  
- [Building](#building) 
- [Current status](#current-status) 
- [License](#license) 
- [Contributing](#contributing) 
- [Credits & Third party components and their licenses](#credits--third-party-components-and-their-licenses)

## Introduction 
PojavLauncher是一个基于Boardwalk的Java版Minecraft启动器，运行在安卓系统上。(https://github.com/zhuowei/Boardwalk). This launcher can launch almost all available Minecraft versions (from rd-132211 to 1.18 snapshots (kinda)), including Combat Test versions. Modding via Forge (up to 1.16.5) and Fabric are also supported. This repository contains source code for Android. For iOS/iPadOS, check out [PojavLauncher_iOS](https://github.com/PojavLauncherTeam/PojavLauncher_iOS).

## Building
To get started, you can just get prebuilt app from [stable release](https://github.com/PojavLauncherTeam/PojavLauncher/releases) or [automatic builds](https://github.com/PojavLauncherTeam/PojavLauncher/actions). If you want to build after launcher code changes, follow steps below.

Will be moved to **BUILDING.md**
### Java Runtime Environment (JRE)
- 安卓可用JRE [here](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u), also the build script [here](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch).
- Follow build instruction on build script [README.md](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/blob/buildjre8/README.md).
- 你可以在这里获取 [CI auto builds](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/actions).
- Either get `jre8-pojav` artifact from auto builds, or do splitting by yourself:</br>
        - Get JREs for all of 4 supported architectures (arm, arm64, x86, x86_64) </br> 
        - Split JRE into parts:</br>
                Platform-independent: .jar files, libraries, configs, etc...</br>
                Platform-dependent: .so files, etc...</br>
        - Create:</br>
                file named `universal.tar.xz` with all platform-independent files</br>
                4 files named `bin-<arch>.tar.xz` with all platform-dependent files per-architecture</br>
        - Put these in `assets/components/jre/` folder</br>
        - (If needed) update the Version file with the current date</br>

### LWJGL
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
- Then, run these commands ~~build use Android Studio~~.
```
# Build GLFW stub
./gradlew :jre_lwjgl3glfw:build
# mkdir app_pojavlauncher/src/main/assets/components/internal_libs
rm app_pojavlauncher/src/main/assets/components/lwjgl3/lwjgl-glfw-classes.jar
cp jre_lwjgl3glfw/build/libs/jre_lwjgl3glfw-3.2.3.jar app_pojavlauncher/src/main/assets/components/lwjgl3/lwjgl-glfw-classes.jar
        
# Build the launcher
./gradlew :app_pojavlauncher:assembleDebug
```
(Replace `gradlew` to `gradlew.bat` if you are building on Windows).

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
- Minecraft `21w10a` or newer are currently not yet supported due to the new GLSL usage. Fortunately, a workaround is provided and built into the launcher.
- In 1.16 and up, spawn eggs banners are white (you can fix this by switching renderer
to `gl4es 1.1.5`, only works on 1.16 and up, do not use under this version, as the texture
will bug out when hit a mob)
- Controller mods aren't working.
- Random crashes could happen very often on Android 5.x during game load or join world.
- With big modpacks textures could be messed up
- probably more, that's why we have a bug tracker ;) 

## License
- PojavLauncher is licensed under [GNU GPLv3](https://github.com/khanhduytran0/PojavLauncher/blob/master/LICENSE).

## Contributing
Contributions are welcome! We welcome any type of contribution, not only code.
Any code change should be submitted as a pull request. The description should explain what the code does and give steps to execute it.

## Credits & Third party components and their licenses (if available)
- [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
- Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- [GL4ES](https://github.com/PojavLauncherTeam/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).<br>
- [OpenJDK](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).<br>
- [LWJGL3](https://github.com/PojavLauncherTeam/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
- [LWJGLX](https://github.com/PojavLauncherTeam/lwjglx) (LWJGL2 API compatibility layer for LWJGL3): unknown license.<br>
- [Mesa 3D Graphics Library](https://gitlab.freedesktop.org/mesa/mesa): [MIT License](https://docs.mesa3d.org/license.html).
- [pro-grade](https://github.com/pro-grade/pro-grade) (Java sandboxing security manager): [Apache License 2.0](https://github.com/pro-grade/pro-grade/blob/master/LICENSE.txt).
- [xHook](https://github.com/iqiyi/xHook) (Used for exit code trapping): [MIT and BSD-style licenses](https://github.com/iqiyi/xHook/blob/master/LICENSE).
- [libepoxy](https://github.com/anholt/libepoxy): [MIT License](https://github.com/anholt/libepoxy/blob/master/COPYING).
- [virglrenderer](https://github.com/PojavLauncherTeam/virglrenderer): [MIT License](https://gitlab.freedesktop.org/virgl/virglrenderer/-/blob/master/COPYING).
