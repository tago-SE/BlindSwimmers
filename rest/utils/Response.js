
/**
 *  Sends a JSON response object 
 * 
 * @param {*} res           response 
 * @param {*} statusCode    http-status code 
 * @param {*} object        object to send 
 */
var sendObject = function sendResponseObject(res, statusCode, object) {
    res.writeHead(statusCode, {'Content-Type': 'application/json'});
    res.end(JSON.stringify(object));
}

module.exports = {sendObject: sendObject};