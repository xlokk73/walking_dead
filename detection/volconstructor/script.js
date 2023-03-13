console.log("[*] Script loaded");

rpc.exports = {
  dumpDex: function (){
    console.log("[*] Dumping dex function loaded!");
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
    
        // find application path to store the dump
        var currentApplication = Java.use('android.app.ActivityThread').currentApplication();
        var path = currentApplication.getFilesDir().getAbsolutePath();
    
        var counter = 0;
        // Iterate over all loaded modules to find the one that contains the class
        console.log("[*] Checking modules");
        Process.enumerateModules({
          onMatch: function (module) {
            // Check if the module name contains "dex"
            // console.log("[*] Checking module " + module.name);
            if (module.name.includes(dlc + ".odex")) {
              var base = module.base;
              var size = module.size;
              console.log("[*] Found module " + module.name + " at " + base + " with size " + size);
              
              // Dump the module
              console.log("[*] Dumping " + module.name);
              var ranges = Process.enumerateRangesSync({protection: 'r--', coalesce: true});
              for (var i = 0; i < ranges.length; i++) {
                var range = ranges[i];
                if (range.base.equals(ptr(module.base))) {
                  var filename = path + "/" + module.name + "_" + range.base.toString() + ".dump";
                  console.log("[*] Writing " + range.size + " bytes to " + filename);
                  var fd = new File(filename, 'wb');
                  fd.write(Memory.readByteArray(range.base, range.size));
                  fd.flush();
                  fd.close();
                  console.log("[*] Done, sending filename!");
                  // Send the path of the dumped module
                  send({dataType: "dexToDump" , dataContent: filename});
                  break;
                }
              }
            }
          },
          onComplete: function () {
            console.log("[*] Module enumeration complete");
          }
        });
      }
    })
  },

  traceIntent: function (){
    console.log("[*] Tracing Intent function...");
    Java.perform(function () {

      // Define the regular expression to match the desired URI format
      var uriRegex = /^(\w+):\/\/(\w+)\/(.+)$/;

      // Find the Intent constructor that takes two arguments
      var Intent = Java.use("android.content.Intent");
      var intentConstructor = Intent.$init.overload('java.lang.String', 'android.net.Uri');

      console.log("[*] Overloading contrsuctor");
      // Hook the constructor to intercept the intents we're interested in
      intentConstructor.implementation = function (action, uri) {

        var uriString = uri.toString();
        var uriMatch = uriRegex.exec(uriString);
        // Check if the intent matches the desired format
        if (action == "android.intent.action.VIEW" && uriMatch != null) {

          // // Log the intent's parameters in string format
          // console.log("Intent parameters:");
          // console.log("- Action: " + action);
          // console.log("- Scheme: " + uriMatch[1]);
          // console.log("- Host: " + uriMatch[2]);
          // console.log("- Path: " + uriMatch[3]);

          // Send the intent's parameters to the Python script
          var myDataContent = {
            "action": action,
            "uri": uri.toString()
          };
          send({dataType: "intentInfo", dataContent: myDataContent});
          //send(payload);
        }

        // Call the original constructor to create the intent
        var intent = this.$init(action, uri);
        return intent;
      };
    });
  }
};
