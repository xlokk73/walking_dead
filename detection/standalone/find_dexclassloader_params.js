Java.perform(function () {
  // find the DexClassLoader class
  var DexClassLoader = Java.use("dalvik.system.DexClassLoader");

  // hook the constructor
  DexClassLoader.$init.overload('java.lang.String', 'java.lang.String', 'java.lang.String', 'java.lang.ClassLoader').implementation = function (dexPath, optimizedDirectory, librarySearchPath, parent) {
    console.log("[*] DexClassLoader constructor hooked");
    console.log("[*] dexPath: " + dexPath);
    console.log("[*] optimizedDirectory: " + optimizedDirectory);
    console.log("[*] librarySearchPath: " + librarySearchPath);
    console.log("[*] parent: " + parent);

    // call the original constructor
    this.$init(dexPath, optimizedDirectory, librarySearchPath, parent);
  };
});

