# PojavLauncher
An open source Minecraft: Java Edition launcher for Android based from Boardwalk. Support up-to Minecraft 1.11.<br>
`gl4es` branch is the current development.

## iOS version?
- Impossible for directly run. RoboVM is AOT Compiler. You could also try UTM with Android-x86.

## Known issues
- Some Huawei devices can't run Minecraft or OptiFine.
- Can't run multiple versions at same time.

## FAQ
### • Installing OptiFine?
Follow steps [here](https://github.com/khanhduytran0/PojavLauncher/wiki/Install-OptiFine).

### • Installing Forge?
Not implemented yet.

### • Unable to drag item in inventory?
Disable touchscreen mode. Open Minecraft Settings -> Controls -> Touchscreen Mode: Toggle to OFF.

### • Unable to install Minecraft 1.9 or above, can't convert library `net.java.dev.jna`
Increase max DX references. Launcher Options -> Settings -> Increase max DX references to 8k.

### • Other bugs or still not working?
#### Report an issue with
- Full error log: press **Show more** and copy.
#### If it isn't a Minecraft crash
- Device name
- Android version 

## OptiFine worked in PojavLauncher 2.4.2
|Android version  |  9.0 |???|???|Manually Android 10|9.0|
|-----------------|------|---|---|-----|-----|
|Minecraft version|1.7.10|1.8|1.9| 1.10| 1.11|
|OptiFine         |OptiFine_1.7.10_HD_U_E7|???|???|OptiFine_1.10_HD_U_H5|OptiFine_1.11_HD_U_F5|
|Status           |[Worked](https://youtu.be/In_EPebQG7Q)|???|???|[Worked (manually)](https://youtu.be/TJeJcPFgzcI)|[Worked (with 1 hack)](https://youtu.be/eIawM9UmQ88)

## License
- This launcher and Boardwalk are licensed under (https://github.com/khanhduytran0/PojavLauncher/blob/master/LICENSE)[Apache License 2.0].

## Third party licenses
- LegacyLauncher: (unknown license).<br>
- Android Support Libraries & DX Dexer: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- gl4es: [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).<br>
- BusyBox: [GNU GPLv2 License](https://busybox.net/license.html).<br>
- OpenJDK: [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).<br>
- PRoot: [GNU GPLv2 License](https://github.com/termux/proot/blob/master/COPYING).<br>
- TheQVD XVnc Pro: [GNU GPLv3 License](https://github.com/theqvd/qvd-client-android/blob/master/LICENSE.txt).
- LWJGL 2: [Legacy LWJGL License](http://legacy.lwjgl.org/license.php.html).<br>
