[![Android CI](https://github.com/PojavLauncherTeam/PojavLauncher/workflows/Android%20CI/badge.svg)](https://github.com/PojavLauncherTeam/PojavLauncher/actions)
[![Crowdin](https://badges.crowdin.net/pojavlauncher/localized.svg)](https://crowdin.com/project/pojavlauncher)
[![Discord](https://img.shields.io/discord/724163890803638273.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/6RpEJda)
# PojavLauncher

## Navigation
- [Introduction](#introduction)
- [Building](#building)
- [Current status](#current-status)
- [License](#license)
- [Contributing](#contributing)
- [Credits & Third party components and it's licenses](#credits--third-party-components-and-its-licenses)


## Introduction
PojavLauncher is a Minecraft: Java Edition launcher for Android based on [Boardwalk](https://github.com/zhuowei/Boardwalk). This launcher can launch a variety large of Minecraft versions (from 1.6 to latest 1.17 snapshot, even Combat Test versions). Modding via Forge and Fabric is also supported,  but Forge is currently limited to 1.13.2 and below.

## Building
Will be moved to **BUILDING.md**
### Java Runtime Environment (JRE)
- JRE for Android is [here](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u), also the build script [here](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch).
- Follow build instruction on build script [README.md](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/blob/buildjre8/README.md).
- You can also get [CI auto builds](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/actions).
- Spliting JRE and put to the launcher: **coming soon**.

### LWJGL and GLFW
- **Coming soon**

### The Launcher
- Because languages are auto added by Crowdin, so need to run language list generator before build. In this directory, run:
```
# On Linux, Mac OS:
chmod +x scripts/languagelist_updater.sh
bash scripts/languagelist_updater.sh

# On Windows:
scripts\languagelist_updater.bat
```
- Then, build use Android Studio.

## Current status
- [x] OpenJDK 9 Mobile port: ARM32, ARM64, x86, x86_64.
- [x] OpenJDK 8 Mobile port: ARM64, x86, x86_64
- [x] Mod installer headless
- [ ] Mod installer with GUI. Little run on `Caciocavallo` project.
- [x] OpenGL in OpenJDK environment
- [x] OpenAL (work on most devices)
- [x] Support Minecraft 1.12.2 and below. Used [lwjglx](https://github.com/PojavLauncherTeam/lwjglx), a LWJGL2 compatibility layer for LWJGL3.
- [x] Support Minecraft 1.13 and above. Used [GLFW stub](https://github.com/PojavLauncherTeam/lwjgl3-glfw-java).
- [ ] Game surface zooming.
- [x] New input pipe rewritten to native code to boost performance.
- [ ] More...

## License
- PojavLauncher is licensed under [GNU GPLv3](https://github.com/khanhduytran0/PojavLauncher/blob/master/LICENSE).

## Contributing
Contributing are welcome! We welcome any type of contribution, not only code. Any code change should be submitted as a pull request. The description should explain what the code does and give steps to execute it.

## Credits & Third party components and it's licenses
- [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
- Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- [GL4ES](https://github.com/ptitSeb/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).<br>
- [OpenJDK](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).<br>
- [LWJGL3](https://github.com/PojavLauncherTeam/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
- [LWJGLX](https://github.com/PojavLauncherTeam/lwjglx) (LWJGL2 API compatibility layer for LWJGL3): unknown license.<br>
