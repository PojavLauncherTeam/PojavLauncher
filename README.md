[![badge](https://api.travis-ci.com/PojavLauncherTeam/PojavLauncher.svg?branch=gl4es)](https://travis-ci.com/github/PojavLauncherTeam/PojavLauncher)
[![Crowdin](https://badges.crowdin.net/pojavlauncher/localized.svg)](https://crowdin.com/project/pojavlauncher)
# PojavLauncher
An open source Minecraft: Java Edition launcher for Android based from Boardwalk. Support up-to Minecraft 1.11
(Codes has not been sorted yet, it contains ton of classes without packing to module).

Discord server: https://discord.gg/6RpEJda

## iOS version?
- Impossible for directly run. There's RoboVM but is AOT Compiler. You could also try UTM with Android-x86.
- OpenJDK 9 and GL4ES has iOS port so maybe run in a jailbreaked device. I don't have a Mac OS X device to build one.

## Installing OptiFine?
Follow steps [here](https://github.com/khanhduytran0/PojavLauncher/wiki/Install-OptiFine).

## Installing Forge?
Not implemented yet.

## Known issues
- Some Huawei devices can't run Minecraft or OptiFine.
- Can't run multiple versions at same time.
- V2 will not work on Android 6 and return error `ClassNotFoundException`.

## FAQ
### • Unable to drag item in inventory?
Disable touchscreen mode. Open Minecraft Settings -> Controls -> Touchscreen Mode: Toggle to OFF.

### • Unable to install Minecraft 1.9 or above, can't convert library `net.java.dev.jna`?
Increase max DX references. Launcher Options -> Settings -> Increase max DX references to 8k.

### • Other bugs or still not working?
#### Report an issue with:
- Full error log: press **Show more** and copy.
#### If it isn't a Minecraft crash:
- Device name
- Android version 

## Minecraft versions worked in PojavLauncher [2.5.3](https://github.com/PojavLauncherTeam/PojavLauncher/releases/tag/untagged-4e80a0c1513abab0c480)

<table>
	  <thead>
		<tr>
		  <th></th>
		  <th align="center" colspan="7">Minecraft version</th>
		</tr>
		<tr>
		  <th>Android</th>
		  <th align="center">1.7.10</th>
		  <th align="center">1.8</th>
		  <th align="center">1.9</th>
		  <th align="center">1.10</th>
		  <th align="center">1.11</th>
		  <th align="center">1.12</th>
		  <th align="center">1.12.2</th>
		</tr>
	  </thead>
	  <tbody>
		<tr>
		  <td>5.x</td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center">???</td>
		  <td align="center">???</td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">No</a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">No</a></td>
		</tr>
		<tr>
		  <td>6.0</td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center">???</td>
		  <td align="center">???</td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">No</a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">No</a></td>
		</tr>
	  </tbody>
	  <tbody>
		<tr>
		  <td>7.x</td>
		  <td align="center">???</td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">No</a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">No</a></td>
		</tr>
		<tr>
		  <td>8.x</td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">Java8</a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">Java8</a></td>
		</tr>
	  </tbody>
	  <tbody>
		<tr>
		  <td>9.0</td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><b>Yes</b></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">Java8</a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">Java8</a></td>
		</tr>
		<tr>
		  <td>10.0</td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">Java8</a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">Java8</a></td>
		</tr>
		<tr>
		  <td>11.0</td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://github.com/khanhduytran0/PojavLauncher/issues/7#issue-586928527"><b>Yes</b></a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">Java8</a></td>
		  <td align="center"><a href="https://stackoverflow.com/a/57861173">Java8</a></td>
		</tr>
	  </tbody>
	</table>


## OptiFine worked in PojavLauncher [2.5.3](https://github.com/PojavLauncherTeam/PojavLauncher/releases/tag/untagged-4e80a0c1513abab0c480)
|Android version  |  9.0 |???|???|Manually Android 10|9.0|
|-----------------|------|---|---|-----|-----|
|Minecraft version|1.7.10|1.8|1.9| 1.10| 1.11|
|OptiFine         |OptiFine_1.7.10_HD_U_E7|???|???|OptiFine_1.10_HD_U_H5|OptiFine_1.11_HD_U_F5|
|Status           |[Worked](https://youtu.be/In_EPebQG7Q)|???|???|[Worked (manually)](https://youtu.be/TJeJcPFgzcI)|[Worked (with 1 hack)](https://youtu.be/eIawM9UmQ88)

## A note about 1.12.x
- Minecraft 1.12.x can be run on Android 8.0 and above by use d8 for desugar and dexing in the latest v2, but will crash in singleplayer.

## License
- PojavLauncher is licensed under [GNU GPLv3](https://github.com/khanhduytran0/PojavLauncher/blob/master/LICENSE).

## Third party components and it's licenses
- (v2) Boardwalk: [Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE).
- (v2) LegacyLauncher: (unknown license).<br>
- (all) Android Support Libraries & DX Dexer: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- (all) gl4es: [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).<br>
- (v3) OpenJDK: [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).<br>
- (all) LWJGL 2: [Legacy LWJGL License](http://legacy.lwjgl.org/license.php.html).<br>
- (v3) LWJGL 3: [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
