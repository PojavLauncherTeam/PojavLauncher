# PojavLauncher
An open source Minecraft: Java Edition launcher for Android based from Boardwalk. Support up-to Minecraft 1.11
(Codes has not been sorted yet, it contains ton of classes without packing to module).

## About this branch
- This branch attempt to launch Minecraft using command line like `dalvikvm` and `app_process`.
- Status: working partial, EGL Context not yet shared, process get crashed if attempt to execute OpenGL command.

## Anything if this successful?
- Able to custom min/max RAM heap.
- Solve some incompatible issues.

## Solution found!
- [twaik/libcw](https://github.com/twaik/libcw) or [shodruky-rhyammer/gl-streaming](https://github.com/shodruky-rhyammer/gl-streaming) but I'm busy at v3.
