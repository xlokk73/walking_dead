#!/bin/sh
adb root
adb install ../../CallZombie/app/build/intermediates/apk/debug/app-debug.apk
adb shell mkdir /data/user/0/com.example.callzombie/files/
adb push ../../CallZombieExtra/app/build/outputs/apk/debug/app-debug.apk /data/user/0/com.example.callzombie/files/dynamic-code.apk
adb shell mkdir /data/user/0/com.example.callzombie/dumps/ 
