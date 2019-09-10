var exec = require('cordova/exec');

exports.onCall = function(successCallback, errorCallback) {
    if ( typeof errorCallback !== "function" ) {
        errorCallback = this.errorCallback;
    }
    exec(successCallback, errorCallback, 'PhoneCallTrap', 'onCall', []);
};

exports.errorCallback = function() {
    //console.log("WARNING: PhoneCallTrap errorCallback not implemented");
};

exports.getCurrentState = function(successCallback, errorCallback) {
    if ( typeof errorCallback !== "function" ) {
        errorCallback = this.errorCallback;
    }
    exec(successCallback, errorCallback, 'PhoneCallTrap', 'getCurrentState', []);
};

exports.rejectCall = function(successCallback, errorCallback) {
    if ( typeof errorCallback !== "function" ) {
        errorCallback = this.errorCallback;
    }
    exec(successCallback, errorCallback, 'PhoneCallTrap', 'rejectCall', []);
};

exports.silenceCall = function(successCallback, errorCallback) {
    if ( typeof errorCallback !== "function" ) {
        errorCallback = this.errorCallback;
    }
    exec(successCallback, errorCallback, 'PhoneCallTrap', 'silenceCall', []);
};

