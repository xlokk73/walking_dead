rpc.exports {
  trace_intent: function { 
    console.log("Tracing Intent...");
    Java.perform(function () {
    
      // Define the regular expression to match the desired URI format
      var uriRegex = /^(\w+):\/\/(\w+)\/(.+)$/;
    
      // Find the Intent constructor that takes two arguments
      var Intent = Java.use("android.content.Intent");
      var intentConstructor = Intent.$init.overload('java.lang.String', 'android.net.Uri');
    
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
          var data = {
            "action": action,
            "uri": uri.toString()
          };
          send(data);
        }
    
        // Call the original constructor to create the intent
        var intent = this.$init(action, uri);
        return intent;
      };
    });
  }
};
