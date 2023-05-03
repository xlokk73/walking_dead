import subprocess

def print_logcat():
    process = subprocess.Popen(["adb", "logcat"], stdout=subprocess.PIPE)
    while True:
        output = process.stdout.readline().decode()
        if output == '' and process.poll() is not None:
            break
        if output:
            print(output.strip())

print_logcat()
