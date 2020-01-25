class Entry {
    
    constructor() {//Constructor should have restrictions on what values the variables can take.
        this.x = undefined;
        this.y = undefined;
        this.z = undefined;
        this.user_id = undefined;//User_id can i be used to specifi a person or is it just a arbritary number? GDPR.
        this.session_id = undefined;
        this.time = undefined;
    }

    containsValidSensorData() {
        return this.x !== undefined && this.y !== undefined && this.z;
    }

    containsValidMetaData() {
        return true; // TODO: Change this to check if timestamp, user_id and session_id is present 
    }

    validateState() {
        return this.containsValidMetaData() && this.containsValidSensorData();
    }

}
module.exports = Entry;