import subprocess
import re

print("Starting script")

# This function checks whether the last character in the line is a number
def last_char_nums(line):
    return re.search(r'\d+$', line)

# This function checks whether the line contains a deep link
def contains_deep_link(line):
    if "walkingdead" in line:
        return True
    # if "android.intent.action.VIEW" in line:
    #     if "https://www.example.com" in line:
    #         return True
    # return False

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

# This function extracts the intent information from the line
def extract_intent_info(line):
    zombie_app = get_deep_link_handler(line)

    # case 1: deep link is navigated to from browser
    if (last_char_nums(line)):
        print("[INFO] case: last char is a number")
        uid = get_uid(line)
        package = get_package_from_uid(uid)

    # case 2: deep link is navigated to from app
    else:
        print("[INFO] case: last char is not a number")
        package = get_calling_package(line)

    
    print("Caller: " + package)
    print("Zombie App: " + zombie_app)

# Start the logcat process
logcat = subprocess.Popen(['adb', 'logcat'], stdout=subprocess.PIPE)

# Read the logcat output line by line
while True:
    line = logcat.stdout.readline().decode('utf-8').strip()
    if contains_deep_link(line):
        print("[INFO] Deep link detected")
        print("[INFO] " + line)
        extract_intent_info(line)







