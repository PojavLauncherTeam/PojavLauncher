@echo off

set thisdir = "%~dp0"
set langfile = %thisdir%\..\app\src\main\assets\language_list.txt

rm %langfile%
dir %thisdir%\..\app\src\main\res\values-* /s /b > %langfile%

