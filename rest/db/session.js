class Entry {
    
    constructor() {//Remnant from old use of code?
        this.entries = [];
        this.user_id = undefined;
        this.session_id = undefined;
        this.time = undefined;
    }

    validateState() {
        return true;
    }
}

module.exports = Entry;