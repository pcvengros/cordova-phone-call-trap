Cordova PhoneCall Trap
=======================

It is a Apache Cordova plugin to simplify handling phone call status and events in Android devices.
This is a fork from https://github.com/TheBosZ/cordova-phone-call-trap, which is a fork from original project: https://github.com/ichirkin/cordova-phone-call-trap.git

Changes:
- changed the way how to access the plugin. Instead of PhoneCallTrap use cordova.plugin.phonecalltrap.
- updates and fixes to package.json and config.xml
- add getCurrentState from https://github.com/pavelety/cordova-phone-call-trap/commit/cb6d42d9b39184b8644c326ea74487739d73b20c

## Install

    $ cordova plugin add https://github.com/pcvengros/cordova-phone-call-trap.git


## Quick Example

    cordova.plugin.phonecalltrap.onCall(function(state, phone) {
        console.log("CHANGE STATE: " + state);

        switch (state) {
            case "RINGING":
                console.log("Phone is ringing");
                console.log(phone);
                break;
            case "OFFHOOK":
                console.log("Phone is off-hook");
                break;

            case "IDLE":
                console.log("Phone is idle");
                break;
        }
    });
	
## Reject call

    cordova.plugin.phonecalltrap.rejectCall(function() {
        console.log("SUCCESSFULLY REJECTED");
    }, function(error) {
        console.error("THERE WAS AN ERROR REJECTING", error);
    });
	
## Silence call

    cordova.plugin.phonecalltrap.silenceCall(function() {
        console.log("SUCCESSFULLY SILENCED");
    }, function(error) {
        console.error("THERE WAS AN ERROR SILENCING", error);
    });

## Supported platforms

- Android 2.3.3 or higher
- IOS

## License

Cordova PhoneCall Trap is released under the [MIT License](http://www.opensource.org/licenses/MIT).
