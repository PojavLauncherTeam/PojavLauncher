[![Android CI](https://github.com/PojavLauncherTeam/PojavLauncher/workflows/Android%20CI/badge.svg)](https://github.com/PojavLauncherTeam/PojavLauncher/actions)
[![Crowdin](https://badges.crowdin.net/pojavlauncher/localized.svg)](https://crowdin.com/project/pojavlauncher)
[**Discord server**](https://discord.gg/6RpEJda)
# PojavLauncher
A Minecraft: Java Edition launcher for Android based on [Boardwalk](https://github.com/zhuowei/Boardwalk).

## Navigation
- [Introduction](#mainly-features-on-v3)
- [Building](#building)
- [Current status](#current-status)
- [License](#license)
- [Contributing](#contributing)
- [Credits & Third party components and it's licenses](#credits--third-party-components-and-its-licenses)


## Mainly features on v3
- Launch Minecraft 1.6 to latest 1.17 snapshot.
- Launch Forge up to 1.13.2. To install Forge 1.12.2 and below, simply put to mod install launcher. For Forge 1.13.2, use custom arguments.
- Launch Fabric any versions. [How to install Fabric](https://github.com/PojavLauncherTeam/PojavLauncher/wiki/Install-Fabric).

## Building
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
