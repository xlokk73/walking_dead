import frida
device = frida.get_usb_device()
handler_package = "com.example.smszombie"
print("[+] Waiting for zombie process to start...")
# Wait for the process to start
while True:
    try:
        process = device.get_process(handler_package)
        print("[+] Zombie process found: " + str(process.pid))
        break
    except frida.ProcessNotFoundError:
        time.sleep(0.05)

# Attach to the process
print("[+] Hooking process...")
session = device.attach(process.pid)
script = session.create_script(open("zombie.js").read())
script.on('message', on_message)
script.load()

