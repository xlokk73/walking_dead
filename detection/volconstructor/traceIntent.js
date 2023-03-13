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
      // Send the intent's parameters to the Python script
      var myDataContent = {
        "action": action,
        "uri": uri.toString()
      };
      send({dataType: "intentInfo", dataContent: myDataContent});
      //send(myDataContent);
    }

    // Call the original constructor to create the intent
    var intent = this.$init(action, uri);
    return intent;
  };
});
