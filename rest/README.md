# REST-API

## Query entries

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
