class Entry {
    
    constructor() {
        this.x = undefined;
        this.y = undefined;
        this.z = undefined;
        this.user_id = undefined;
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