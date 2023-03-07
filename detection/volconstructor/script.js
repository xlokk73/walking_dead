console.log("[*] Dumping dex script loaded!");
Java.perform(function(){
  let dexclassLoader = Java.use("dalvik.system.DexClassLoader");
  console.log("[*] Hooking DexFile.loadDex")
  // Waiting for dynamic code loading to start
  dexclassLoader.$init.implementation = function(dlc_path,b,c,d){
    console.log("[*] Absolute loaded path: " + dlc_path);

    // calling original method
    this.$init(dlc_path,b,c,d)

    // obtaining dynamic loaded code file name (also appears in memory that way)
    const parts = dlc_path.split('/');
    const fileNameWithExt = parts.pop();
    const dlc = fileNameWithExt.split('.')[0];

    console.log("[*] The app loaded: `" + dlc + "`");

    // dump dex after it is loaded
    // start script when the message containing package is received
    recv('path', function onMessage(message) { 
      console.log("[*] Received poke message: " + message.payload);
      const path = message.payload;

      var counter = 0;
      // Iterate over all loaded modules to find the one that contains the class
      console.log("[*] Checking modules");
      Process.enumerateModules({
        onMatch: function (module) {
          // Check if the module name contains "dex"
          // console.log("[*] Checking module " + module.name);
          if (module.name.includes(dlc)) {
            var base = module.base;
            var size = module.size;
            console.log("[*] Found module " + module.name + " at " + base + " with size " + size);
            
            // Dump the module
            console.log("[*] Dumping " + module.name);
            var ranges = Process.enumerateRangesSync({protection: 'r--', coalesce: true});
            for (var i = 0; i < ranges.length; i++) {
              var range = ranges[i];
              if (range.base.equals(ptr(module.base))) {
                var filename = path + module.name + "_" + range.base.toString() + ".bin";
                console.log("[*] Writing " + range.size + " bytes to " + filename);
                var fd = new File(filename, 'wb');
                fd.write(Memory.readByteArray(range.base, range.size));
                fd.flush();
                fd.close();
                console.log("[*] Done!");
                // Send the path of the dumped module
                send(filename);
                break;
              }
            }
          }
        },
        onComplete: function () {
          console.log("[*] Module enumeration complete");
        }
      });
    });
  }
})







