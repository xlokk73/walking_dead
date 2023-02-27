#!/bin/sh
adb root
adb install ../../SMSZombie/app/build/intermediates/apk/debug/app-debug.apk
adb shell mkdir /data/user/0/com.example.smszombie/files/
adb push ../../SMSZombieExtra/app/build/outputs/apk/debug/app-debug.apk /data/user/0/com.example.clipboardzombie/files/dynamic-code.apk
adb shell mkdir /data/user/0/com.example.smszombie/dumps/ 
