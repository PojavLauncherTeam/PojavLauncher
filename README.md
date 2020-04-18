# PojavLauncher
A Minecraft: Java Edition launcher for Android based from Boardwalk. Support up-to Minecraft 1.11

## Source code
- This repository currently for bug tracking.
- There are some open source parts used in this launcher.

## Planning
- There's no planning.

## iOS version?
- Impossible for directly run. RoboVM is AOT Compiler. You could also try UTM with Android-x86.

## Installing OptiFine?
Follow steps [here](https://github.com/khanhduytran0/PojavLauncher/wiki/Install-OptiFine).

## Installing Forge?
Not implemented yet.

## Known issues
- Some Huawei devices can't run Minecraft or OptiFine.
- Can't run multiple versions at same time.

## Got some bugs?
### • Unable to drag item in inventory?
Disable touchscreen mode. Open Minecraft Settings -> Controls -> Touchscreen Mode: Toggle to OFF.

### • Unable to install Minecraft 1.9 or above, can't convert library `net.java.dev.jna`
Increase max DX references. Launcher Options -> Settings -> Increase max DX references to 8k.

### • Other bugs or still not working?
#### Report an issue with:
- Full error log: press **Show more** and copy.
#### If it isn't a Minecraft crash:
- Device name
- Android version 

## Minecraft versions worked in PojavLauncher 2.4.2

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
		  <td align="center">???</td>
		  <td align="center">???</td>
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
	  </tbody>
	</table>


## OptiFine worked in PojavLauncher 2.4.2
|Android version  |  9.0 |???|???|Manually Android 10|9.0|
|-----------------|------|---|---|-----|-----|
|Minecraft version|1.7.10|1.8|1.9| 1.10| 1.11|
|OptiFine         |OptiFine_1.7.10_HD_U_E7|???|???|OptiFine_1.10_HD_U_H5|OptiFine_1.11_HD_U_F5|
|Status           |[Worked](https://youtu.be/In_EPebQG7Q)|???|???|[Worked (manually)](https://youtu.be/TJeJcPFgzcI)|[Worked (with 1 hack)](https://youtu.be/eIawM9UmQ88)

## Credits
- [BoardwalkApp](https://github.com/BoardwalkApp) for original code.
- pTitSeb and lunixbochs for [gl4es](https://github.com/pTitSeb/gl4es).
- ...more (in-app).
