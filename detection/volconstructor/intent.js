Java.perform(function () {

    // Find the Intent constructor that takes two arguments
    var Intent = Java.use("android.content.Intent");
    var intentConstructor = Intent.$init.overload('java.lang.String', 'android.net.Uri');

    // Hook the constructor to intercept the intents we're interested in
    intentConstructor.implementation = function (action, uri) {

        // Check if the intent matches the desired format
        if (action == "android.intent.action.VIEW" && uri.toString().startsWith("walkingdead://smszombie/")) {

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
