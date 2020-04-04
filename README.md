# PojavLauncher
A Minecraft: Java Edition launcher for Android based from Boardwalk. Support up-to Minecraft 1.12

# Source code
Not yet, this repository currently for bug tracking.

# Planning
- ~~If [exagear_windows_emulator](https://github.com/khanhduytran0/exagear_windows_emulator) repository is runnable then I will make PojavLauncher v3.~~ There are too many probelms when switching to Exagear Windows Emulator code, read below.
- Adding custom version.
## Problem before switching to
- Source code not working.
## Problem after switching to
- Low performance.
- No mouse pointer.
- Can't compatible with some devices running Android 10 and above. [Reason?](https://issuetracker.google.com/issues/128554619)

# Got some bugs?
### • Unable to drag item in inventory?
Disable touchscreen mode. Open Minecraft Settings -> Controls -> Touchscreen Mode: Toggle to OFF.

### • Unable to install Minecraft 1.9 or above, can't convert library `net.java.dev.jna`
Increase max DX references. Launcher Options -> Settings -> Increase max DX references to 8k.

# Minecraft versions worked in PojavLauncher 2.4.2

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
		  <td align="center">???</td>
		  <td align="center">???</td>
		  <td align="center">???</td>
		  <td align="center">???</td>
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


# OptiFine worked in PojavLauncher 2.4.2
|Android version  |  9.0 |???|???|Manually Android 10|9.0|
|-----------------|------|---|---|-----|-----|
|Minecraft version|1.7.10|1.8|1.9| 1.10| 1.11|
|OptiFine         |OptiFine_1.7.10_HD_U_E7|???|???|OptiFine_1.11_HD_U_H5|OptiFine_1.11_HD_U_F5|
|Status           |[Worked](https://youtu.be/In_EPebQG7Q)|???|???|[Worked (manually)](https://youtu.be/TJeJcPFgzcI)|[Worked (with 1 hack)](https://youtu.be/eIawM9UmQ88)
