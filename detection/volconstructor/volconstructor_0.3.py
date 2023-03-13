# original source: https://book.hacktricks.xyz/mobile-pentesting/android-app-pentesting/frida-tutorial/frida-tutorial-2
# Waits for the process to start and then attaches to it

import frida
import time
from adbutils import adb
import os
import re
import subprocess
import argparse
import sys

## Global variables
DOWNLOAD_COUNT = 0 
MAX_LOGCAT_LINES = 10000
ADB_DEVICE = adb.device()
PACKAGE = ""
API = None

## Functions


# Function to download the file
def download(item):
    global DOWNLOAD_COUNT
    device = adb.device()

    print("[+] Pulling " + item)
    file_name = os.path.basename(item)
    device.sync.pull(item, "outputs/" + str(DOWNLOAD_COUNT) + "__" + file_name)
    DOWNLOAD_COUNT += 1

# This function checks whether the last character in the line is a number
def last_char_nums(line):
    return re.search(r'\d+$', line)


# This function extracts the UID from the line
# example input: "ActivityTaskManager: START u0 {act=android.intent.action.VIEW cat=[android.intent.category.BROWSABLE] dat=walkingdead://smszombie/?url=http://192.168.1.134:1313 flg=0x14000000 cmp=com.example.smszombie/.WebViewActivity (has extras)} from uid 10121"
# example output: "10121"
def get_uid(line):
    uid = line.split(" ")[-1]
    return uid

# This function extracts the SMSZombie from the line
# example input: "ActivityTaskManager: "ActivityTaskManager: START u0 {act=android.intent.action.VIEW cat=[android.intent.category.BROWSABLE] dat=walkingdead://smszombie/?url=http://192.168.1.134:1313 flg=0x14000000 cmp=com.example.smszombie/.WebViewActivity (has extras)} from uid 10121"
# example output: "com.example.smszombie/.WebViewActivity"
def get_deep_link_handler(line):
    smszombie = line.split("cmp=")[-1].split(" ")[0]
    return smszombie

# This function finds the package corresponding to the UID
# it starts by running the command "adb shell pm list packages -U -f -3"
# this command lists all packages that have been updated recently
# it then searches for the UID in the output
# if the UID is found, the package name is extracted by finding what is after "base.apk="
# example input: "10121"
# example output: "com.android.chrome"
def get_package_from_uid(uid):
    packages = subprocess.check_output(['adb', 'shell', 'pm', 'list', 'packages', '-U', '-f', '-3']).decode('utf-8').strip()
    for package in packages.split("package:"):
        if uid in package:
            return package.split(" ")[0].split("=")[-1]

# This function extracts the package name from the intent line
def get_calling_package(string):
    package = string.split("callingPackage: ")[1].split(";")[0].strip()
    return package


# This function extracts the file path from the line
def get_path(package):
    global ADB_DEVICE
    pattern = r'^package:([^\s]+)='

    line = ADB_DEVICE.shell("pm list packages -f | grep " + package)

    # Use regular expressions to extract the file path from the line
    match = re.search(pattern, line)
    if match:
        file_path = match.group(1)
        print("[+] derived filepath: " + file_path)
        return file_path

    print("ERROR: Could not extract file path from line: " + line)
    return None


# This function extracts the intent information from the line
# case 1: deep link is navigated to from browser
# example line: 01-25 14:04:48.976   528  3274 I ActivityTaskManager: START u0 {act=android.intent.action.VIEW cat=[android.intent.category.BROWSABLE] dat=walkingdead://smszombie/?url=http://192.168.1.134:1313 flg=0x14000000 cmp=com.example.smszombie/.WebViewActivity (has extras)} from uid 10121"

# case 2: deep link is navigated to from app
# example line: "01-25 13:07:54.341   528  2776 W ActivityTaskManager: Background activity start [callingPackage: com.metasploit.stage; callingUid: 10147; isCallingUidForeground: false; callingUidHasAnyVisibleWindow: false; callingUidProcState: SERVICE; isCallingUidPersistentSystemProcess: false; realCallingUid: 10147; isRealCallingUidForeground: false; realCallingUidHasAnyVisibleWindow: false; realCallingUidProcState: SERVICE; isRealCallingUidPersistentSystemProcess: false; originatingPendingIntent: null; isBgStartWhitelisted: false; intent: Intent { act=android.intent.action.VIEW dat=walkingdead://smszombie/?url=http://192.168.1.134:1313 flg=0x10000000 cmp=com.example.smszombie/.WebViewActivity }; callerApp: ProcessRecord{4302d2d 3323:com.metasploit.stage/u0a147}]"
def extract_intent_info(line):
    calledFromApp = False

    if "callingUid" in line:
        calledFromApp = True
        match = re.search("callingUid: (\d+)", line)
        sender_uid = match.group(1)
    else:
        calledFromApp = False
        match = re.search("from uid (\d+)", line)
        sender_uid = match.group(1)

    sender_package = get_package_from_uid(sender_uid)

    # Extract the deeplink
    match = re.search("dat=([^\s]+)", line)
    deeplink = match.group(1)

    # Extract the package name
    match = re.search("cmp=([^/]+)", line)
    package_name = match.group(1)

    print("[+] Sender UID: ", sender_uid)
    print("[+] Sender Package: ", sender_package)
    print("[+] Deeplink: ", deeplink)
    print("[+] Package Name: ", package_name)

    return sender_package, package_name    

def find_handler(deeplink):
    global PACKAGE
    print("[+] Finding handler for deeplink: " + deeplink)
    global ADB_DEVICE
    ADB_DEVICE.shell("logcat --clear")
    stream = ADB_DEVICE.shell("logcat", stream=True)
    with stream:
        f = stream.conn.makefile()
        for _ in range(MAX_LOGCAT_LINES):
            line = f.readline()
            if deeplink in line:
                print("[+] Found line: " + line)
                sender_package, handler_package = extract_intent_info(line)
                # return handler_package if the sender package is the package we are investigating
                if sender_package == PACKAGE:
                    return sender_package, handler_package

# Function to handle messages from the script
def on_message(message, data):
    print("[+] Raw message", message);

    if(message["payload"]["dataType"] == "dexToDump"):
        print("[+] Received: " + message['payload']["dataContent"])
        download(message["payload"]["dataContent"])

    elif (message["payload"]["dataType"] == "intentInfo"):
        # Print the intent parameters received from the Frida script
        print("[+] Intent parameters:")
        print("[+] - Action: " + message["payload"]["dataContent"]["action"])
        print("[+] - URI: " + message["payload"]["dataContent"]["uri"])
        sender_package, handler_package = find_handler(message["payload"]["dataContent"]["uri"])
        download(get_path(handler_package))

        # Dump the dex class
        global ADB_DEVICE
        device = frida.get_usb_device()

        print("[+] Waiting for zombie process to start...")
        # Wait for the process to start
        while True:
            try:
                process = device.get_process(handler_package)
                print("[+] Zombie process found: " + str(process.pid))
                break
            except frida.ProcessNotFoundError:
                time.sleep(0.01)

        # Attach to the process
        print("[+] Hooking process...")
        session = device.attach(process.pid)
        script = session.create_script(open("zombie.js").read())
        script.on('message', on_message)
        script.load()
        #api = script.exports
        #api.dump_dex()
        #api.trace_intent()


    else: 
        print("[+] Error: message dataType not supported");

# receives package that iwll be investiagted
def main(package):
    global PACKAGE
    PACKAGE = package

#    script_file = "intent.js"
#    # Load the script from the file
#    with open(script_file, "r") as f:
#        script_code = f.read()
#
#    # Attach to the app and run the script
#    device = frida.get_usb_device()
#    pid = device.spawn([PACKAGE])
#    session = device.attach(pid)
#    script = session.create_script(script_code)
#
#    # Set the callback function to handle messages from the script
#    script.on("message", on_message)
#
#    # Load and run the script
#    script.load()
#    device.resume(pid)
#
#    # Wait for the script to finish
#    input("[+] Press enter to detach...")
#
#    # Detach from the app and clean up
#    session.detach()
#    device.kill(pid)
    
    device = frida.get_usb_device()

    #script_file = "script.js"
    # Load the script from the file
    #with open(script_file, "r") as f:
    #    script_code = f.read()

    # Attach to the app and run the script
    device = frida.get_usb_device()
    print("[+] Spawning " + package);
    pid = device.spawn([PACKAGE])
    session = device.attach(pid)

    #script = session.create_script(script_code)

    script = session.create_script(open("meterpreter.js").read())

    # Set the callback function to handle messages from the script
    script.on("message", on_message)

    # Load and run the script
    script.load()
    device.resume(pid)

    api = script.exports
    api.dump_dex()
    api.trace_intent()


    # Wait for the script to finish
    input("[+] Press enter to detach...")

    # Detach from the app and clean up
    session.detach()
    device.kill(pid)


# Create an argument parser object
parser = argparse.ArgumentParser(description="A python script with a main function")

# Add an argument to the parser
parser.add_argument("arg", help="The argument to be passed to the main function")

# Check if the script is run directly
if __name__ == "__main__":
    # Parse the arguments from the command line
    args = parser.parse_args()

    # Call the main function with the argument
    main(args.arg)
