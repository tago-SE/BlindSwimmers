var express = require('express');
var router = express.Router();
var HttpStatus = require('http-status-codes');
const EntriesDao = require('../db/entries_dao');
const Entry = require('../db/entry');
const Response = require('../utils/Response');
const DbUtils = require('../db/utils');
const DEFEAULT_LIMIT = 100


/**
 * Query entries inside the repository. 
 */
router.get('/', function(req, res, next) {

    var queries = [];

    if (req.query.x !== undefined)
        queries.push({['x']: req.query.x});
    if (req.query.y !== undefined)
        queries.push({['y']: req.query.y});
    if (req.query.y !== undefined)
        queries.push({['z']: req.query.z});
    if (req.query.user_id !== undefined)
        queries.push({['user_id']: req.query.user_id});
    if (req.query.session_id !== undefined)
        queries.push({['session_id']: req.query.session_id});

    // Page operator    
    var page = 0
    if (req.query.page !== undefined) 
        page = Number(req.query.page) - 1 
        if (page < 0) page = 0 

    // Limit operator 
    var limit = DEFEAULT_LIMIT
    if (req.query.limit !== undefined) 
        limit = Number(req.query.limit)
        if (limit > DEFEAULT_LIMIT)
        limit = DEFEAULT_LIMIT

    // Add a find all query if no previous query was found 
    if (queries.length == 0) 
        queries.push({});

    (async () => {  
        try {
            var entries = await EntriesDao.findArrayQueryPagination(queries, page, limit);
            Response.sendObject(res, HttpStatus.OK, entries);
        } catch(err) {
            Response.sendObject(res, HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    })();
});


router.get('/delete', function(req, res, next) {

    var queries = [];

    if (req.query.x !== undefined)
        queries.push({['x']: req.query.x});
    if (req.query.y !== undefined)
        queries.push({['y']: req.query.y});
    if (req.query.y !== undefined)
        queries.push({['z']: req.query.z});
    if (req.query.user_id !== undefined)
        queries.push({['user_id']: req.query.user_id});
    if (req.query.session_id !== undefined)
        queries.push({['session_id']: req.query.session_id});
    if (req.query._id !== undefined)
        queries.push({['_id']: DbUtils.castToId(req.query._id)});

    if (queries.length == 0) {
        Response.sendObject(res, HttpStatus.BAD_REQUEST, "Missing query arguments.");
        return;
    } 
    (async () => {  
        try {
            var result = await EntriesDao.deleteManyByArrayQuery(queries);
            if (result.deletedCount > 0)
                Response.sendObject(res, HttpStatus.OK, result);
            else 
                Response.sendObject(res, HttpStatus.NOT_FOUND, result);
        } catch(err) {
            Response.sendObject(res, HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    })();
});

/**
 * Insert object into repository 
 */
router.post('/insert', function(req, res, next) {
    var entry = new Entry();
    entry.x = req.body.x;
    entry.y = req.body.y;
    entry.z = req.body.z;
    entry.session_id = req.body.session_id;
    entry.user_id = req.body.user_id;
    entry.time = req.body.time;

    // Validates that the object contains all the proper arguments
    if (!entry.validateState()) {
        Response.sendObject(res, HttpStatus.BAD_REQUEST, "Missing body arguments.");
        return;
    } 

    // Attempts to insert the entry into the database
    (async () => {  
        try {
            var result = await EntriesDao.insertOne(entry);
            Response.sendObject(res, HttpStatus.OK, result);
        } catch(err) {
            Response.sendObject(res, HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    })();
});

module.exports = router; 
