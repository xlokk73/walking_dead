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

# This function finds the intent that navigated the browser
def find_intent(package):
    intents = subprocess.check_output(['adb', 'shell', 'dumpsys', 'package', package]).decode('utf-8').strip()
    for intent in intents.split("intent-filter"):
        if "walkingdead" in intent:
            print(intent)

# This function extracts the intent information from the line
# case 1: deep link is navigated to from browser
# example line: 01-25 14:04:48.976   528  3274 I ActivityTaskManager: START u0 {act=android.intent.action.VIEW cat=[android.intent.category.BROWSABLE] dat=walkingdead://smszombie/?url=http://192.168.1.134:1313 flg=0x14000000 cmp=com.example.smszombie/.WebViewActivity (has extras)} from uid 10121"

# case 2: deep link is navigated to from app
# example line: "01-25 13:07:54.341   528  2776 W ActivityTaskManager: Background activity start [callingPackage: com.metasploit.stage; callingUid: 10147; isCallingUidForeground: false; callingUidHasAnyVisibleWindow: false; callingUidProcState: SERVICE; isCallingUidPersistentSystemProcess: false; realCallingUid: 10147; isRealCallingUidForeground: false; realCallingUidHasAnyVisibleWindow: false; realCallingUidProcState: SERVICE; isRealCallingUidPersistentSystemProcess: false; originatingPendingIntent: null; isBgStartWhitelisted: false; intent: Intent { act=android.intent.action.VIEW dat=walkingdead://smszombie/?url=http://192.168.1.134:1313 flg=0x10000000 cmp=com.example.smszombie/.WebViewActivity }; callerApp: ProcessRecord{4302d2d 3323:com.metasploit.stage/u0a147}]"
def extract_intent_info(line):
    if "callingUid" in line:
        match = re.search("callingUid: (\d+)", line)
        sender_uid = match.group(1)
    else:
        match = re.search("from uid (\d+)", line)
        sender_uid = match.group(1)

    # Extract the deeplink
    match = re.search("dat=([^\s]+)", line)
    deeplink = match.group(1)

    # Extract the package name
    match = re.search("cmp=([^/]+)", line)
    package_name = match.group(1)

    print("Sender UID: ", sender_uid)
    print("Deeplink: ", deeplink)
    print("Package Name: ", package_name)

# Start the logcat process
logcat = subprocess.Popen(['adb', 'logcat'], stdout=subprocess.PIPE)

# Read the logcat output line by line
while True:
    line = logcat.stdout.readline().decode('utf-8').strip()
    if contains_deep_link(line):
        print("[INFO] Deep link detected")
        print("[INFO] " + line)
        extract_intent_info(line)







