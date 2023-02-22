from adbutils import adb
import os

d = adb.device()
d.shell("logcat --clear")
stream = d.shell("logcat", stream=True)
with stream:
    f = stream.conn.makefile()
    for _ in range(10): # read 10 lines
        line = f.readline()
        print("Logcat:", line.rstrip())
    f.close()