# REST-API

## Query Entries

Returns a list of entries matching the query parameters. 


#### HTTP Request

    GET: "/entries"

Example:    /entries?session_id="abc123"&user_id="tago-SE"

#### Query Parameters

    
    &x=<float> 
    &y=<float>
    &z=<float>
    &user_id=<string> 
    &session_id=<string>   
    &page=<int>  
    &limit=<int> 

#### HTTP Response 

    {
        ...
    }

---

## Insert Entry 

Returns a insertion response. 


#### HTTP Request

    GET: "/entries/delete"

    BODY: {
        "x": "HEEEEEEEEEY",
        "y": "HEEEEEEEEEY",
        "z": "HEEEEEEEEEY"
    }

Example:    /entries/delete?_id=5e2c409cb8defa0e14f24e55 

#### HTTP Response 

    {
        "insertedCount": 1,
        "ops": [
            {
                "x": "HEEEEEEEEEY",
                "y": "HEEEEEEEEEY",
                "z": "HEEEEEEEEEY",
                "_id": "5e2c47a62693662790a4d1d1"
            }
        ]
    }

---

## Delete Entries 

Returns a delete response 

#### HTTP Request

    GET: "/entries/delete"

Example:    /entries/delete?_id=5e2c409cb8defa0e14f24e55

#### Query Parameters

    
    &x=<float> 
    &y=<float>
    &z=<float>
    &_id=<string>              
    &user_id=<string> 
    &session_id=<string> 

#### HTTP Response 

    {
        "deletedCount": 0
    }

---
