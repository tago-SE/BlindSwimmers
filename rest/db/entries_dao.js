const MongoClient = require('mongodb').MongoClient;

DBNAME = process.env.db_name
URL = process.env.db_url
const COLLECTION_KEY = 'entries';

module.exports = class EntriesDao {

    /**
     * Inserts a object into the database
     * 
     * @param {*} entry             Object to be inserted
     */
    static insertOne(entry) {
        return new Promise(function (resolve, reject) {
            MongoClient.connect(URL, function(err, db) {
                if (err) 
                    reject(err);
                else {
                    db.db(DBNAME).collection(COLLECTION_KEY).insertOne(entry, function(err, res) {
                        if (err)
                            reject(err);
                        else  
                            resolve(res);
                    });
                }
                if (db !== null && db) db.close();
            });
        });
    }

    /**
     * Deletes all entries matching the array query 
     * @param {*} queryArray 
     */
    static deleteManyByArrayQuery(queryArray) {
        var query = {"$and": queryArray }
        return new Promise(function (resolve, reject) {
            MongoClient.connect(URL, function(err, db) {
                if (err) 
                    reject(err);
                else {
                    db.db(DBNAME).collection(COLLECTION_KEY).deleteMany(query, function(err, res) {
                        if (err)
                            reject(err);
                        else  
                            resolve(res);
                    });
                }
                if (db !== null && db) db.close();
            });
        });
    }

    /**
     * Combines multiple queries and puts a pagination filter on top of it to reduce the amount of elements sent back.
     * 
     * @param {*} queryArray        Array containing one or more queries.
     * @param {*} page              Which page should be returned, starts at 0.
     * @param {*} limit             The limit of entities returned for a given page.
     */
    static findArrayQueryPagination(queryArray, page, limit) {
        var query = {"$and": queryArray }
        return new Promise(function (resolve, reject) {
            MongoClient.connect(URL, function(err, db) {
                if (err) {
                    reject(err);
                }
                else {
                    db.db(DBNAME).collection(COLLECTION_KEY).find(query).skip(page*limit).limit(limit).toArray(function (err, res) {
                        if (err) {
                            reject(err);
                        } else {
                            resolve(res);
                        }           
                    });
                }
                if (db !== null && db) db.close();
            });
        });
    }
}