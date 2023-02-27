#!/bin/sh
adb root
adb install smszombie.apk
adb shell mkdir /data/user/0/com.example.clipboardzombie/files/
adb push sms-dynamic-code.apk /data/user/0/com.example.clipboardzombie/files/dynamic-code.apk
adb shell mkdir /data/user/0/com.example.clipboardzombie/dumps/ 
