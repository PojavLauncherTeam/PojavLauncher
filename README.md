<H1 align="center">PojavLauncher</H1>

<img src="https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/app_pojavlauncher/src/main/assets/pojavlauncher.png" align="left" width="130" height="150" alt="PojavLauncher logo">

[![Android CI](https://github.com/PojavLauncherTeam/PojavLauncher/workflows/Android%20CI/badge.svg)](https://github.com/PojavLauncherTeam/PojavLauncher/actions)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/m/PojavLauncherTeam/PojavLauncher)](https://github.com/PojavLauncherTeam/PojavLauncher/actions)
[![Crowdin](https://badges.crowdin.net/pojavlauncher/localized.svg)](https://crowdin.com/project/pojavlauncher)
[![Discord](https://img.shields.io/discord/724163890803638273.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.com/invite/aenk3EUvER) 
[![Twitter Follow](https://img.shields.io/twitter/follow/plaunchteam?color=blue&style=flat-square)](https://twitter.com/PLaunchTeam)

* From [Boardwalk](https://github.com/zhuowei/Boardwalk)'s ashes here comes PojavLauncher!

* PojavLauncher is a launcher that allows you to play Minecraft: Java Edition on your Android device!

* It can run almost every version of Minecraft, allowing you to use .jar only installers to install modloaders such as [Forge](https://files.minecraftforge.net/) and [Fabric](http://fabricmc.net/), mods like [OptiFine](https://optifine.net) and [LabyMod](https://www.labymod.net/en), as well as hack clients like [Wurst](https://www.wurstclient.net/), and much more!

* For more details [check out our wiki](https://pojavlauncherteam.github.io/)
## Some notes to start with
- We do not have an official TikTok account. No one from the dev team makes TikTok videos. 
- The official Twitter for PojavLauncher is [@PLaunchTeam](https://twitter.com/PLaunchTeam). Any others (most notably @PojavLauncher) are fake.

## Navigation
- [Introduction](#introduction)  
- [Getting PojavLauncher](#getting-pojavlauncher)
- [Building](#building) 
- [Current status](#current-status) 
- [License](#license) 
- [Contributing](#contributing) 
- [Credits & Third party components and their licenses](#credits--third-party-components-and-their-licenses-if-available)

## Introduction 
* PojavLauncher is a Minecraft: Java Edition launcher for Android and iOS based on [Boardwalk](https://github.com/zhuowei/Boardwalk). 
* This launcher can launch almost all available Minecraft versions ranging from rd-132211 to 1.21 snapshots (including Combat Test versions). 
* Modding via Forge and Fabric are also supported. 
* This repository contains source code for Android. 
* For iOS/iPadOS, check out [PojavLauncher_iOS](https://github.com/PojavLauncherTeam/PojavLauncher_iOS).

## Getting PojavLauncher

You can get PojavLauncher via three methods:

1. You can get the prebuilt app from [stable releases](https://github.com/PojavLauncherTeam/PojavLauncher/releases) or [automatic builds](https://github.com/PojavLauncherTeam/PojavLauncher/actions).

2. You can get it from Google Play by clicking on this badge:
[![Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=net.kdt.pojavlaunch)

3. You can [build](#building) from source.
## Building
If you want to build from source code, follow the steps below.
### Java Runtime Environment (JRE)
- JRE for Android is [here](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u), and the build script is [here](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch).
- Follow build instruction on build script [README.md](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/blob/buildjre8/README.md).
- You can also get [CI auto builds](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/actions) if you are lazy or are failing to build it for some reason.
* Either get the `jre8-pojav` artifact from auto builds, or split all artifacts by yourself:</br>
   - Get JREs for all of 4 supported architectures (arm, arm64, x86, x86_64) </br> 
      - Split JRE into parts:</br>
                Platform-independent: .jar files, libraries, configs, etc...</br>
                Platform-dependent: .so files, etc...</br>
        - Create:</br>
                A file named `universal.tar.xz` with all platform-independent files</br>
                4 files named `bin-<arch>.tar.xz` with all platform-dependent files per-architecture</br>
        - Put these in the `assets/components/jre/` folder</br>
        - (If needed) update the Version file with the current date</br>

### LWJGL
The build instructions for the custom LWJGL are available over the [LWJGL repository](https://github.com/PojavLauncherTeam/lwjgl3)

### The Launcher
- Because languages are auto-added by Crowdin, you need to run the language list generator before building. In the project directory, run:

* On Linux, Mac OS:
```
chmod +x scripts/languagelist_updater.sh
bash scripts/languagelist_updater.sh
```
* On Windows:
```
scripts\languagelist_updater.bat
```
Then, run these commands ~~or build using Android Studio~~.

* Build GLFW stub:
```
./gradlew :jre_lwjgl3glfw:build
```       
* Build the launcher
```
./gradlew :app_pojavlauncher:assembleDebug
```
(Replace `gradlew` with `gradlew.bat` if you are building on Windows).

## Current status
- [x] ~~OpenJDK 9 Mobile port: ARM32, ARM64, x86, x86_64.~~ Replaced by JRE8.
- [x] OpenJDK 8 Mobile port: ARM32, ARM64, x86, x86_64
- [x] OpenJDK 17 Mobile port: ARM32, ARM64, x86, x86_64
- [x] Headless mod installer
- [x] Mod installer with GUI. Used `Caciocavallo` project for AWT without X11.
- [x] OpenGL in OpenJDK environment
- [x] OpenAL (works on most devices)
- [x] Support for Minecraft 1.12.2 and below. Used [lwjglx](https://github.com/PojavLauncherTeam/lwjglx), a LWJGL2 compatibility layer for LWJGL3.
- [x] Support for Minecraft 1.13 and above. Used [GLFW stub](https://github.com/PojavLauncherTeam/lwjgl3-glfw-java).
- [x] Support for Minecraft 1.17 (22w13a to be exact) and above. Used [Holy GL4ES](https://github.com/PojavLauncherTeam/gl4es-114-extra)
- [x] Game surface zooming.
- [x] New input pipe rewritten to native code to boost performance.
- [x] Rewritten entire controls system (thanks to @Mathias-Boulay)
- [ ] More to come!

## Known Issues
- Controller mods aren't working.
- Random crashes could happen very often on Android 5.x when loading the game or joining a world.
- With big modpacks textures could be messed up
- Probably more, that's why we have a bug tracker ;) 

## License
- PojavLauncher is licensed under [GNU LGPLv3](https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/LICENSE).

## Contributing
Contributions are welcome! We welcome any type of contribution, not only code. For example, you can help the wiki shape up. You can help the [translation](https://crowdin.com/project/pojavlauncher) too!


Any code change to this repository (or iOS) should be submitted as a pull request. The description should explain what the code does and give steps to execute it.

## Credits & Third party components and their licenses (if available)
- [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
- Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- [GL4ES](https://github.com/PojavLauncherTeam/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).<br>
- [OpenJDK](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).<br>
- [LWJGL3](https://github.com/PojavLauncherTeam/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
- [LWJGLX](https://github.com/PojavLauncherTeam/lwjglx) (LWJGL2 API compatibility layer for LWJGL3): unknown license.<br>
- [Mesa 3D Graphics Library](https://gitlab.freedesktop.org/mesa/mesa): [MIT License](https://docs.mesa3d.org/license.html).
- [pro-grade](https://github.com/pro-grade/pro-grade) (Java sandboxing security manager): [Apache License 2.0](https://github.com/pro-grade/pro-grade/blob/master/LICENSE.txt).
- [bhook](https://github.com/bytedance/bhook) (Used for exit code trapping): [MIT license](https://github.com/bytedance/bhook/blob/main/LICENSE).
- [libepoxy](https://github.com/anholt/libepoxy): [MIT License](https://github.com/anholt/libepoxy/blob/master/COPYING).
- [virglrenderer](https://github.com/PojavLauncherTeam/virglrenderer): [MIT License](https://gitlab.freedesktop.org/virgl/virglrenderer/-/blob/master/COPYING).
- Thanks to [MCHeads](https://mc-heads.net) for providing Minecraft avatars.
