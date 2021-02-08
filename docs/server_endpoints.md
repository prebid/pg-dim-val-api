# Server Endpoints 

Dimension Value API hosts several endpoints. 
The upload endpoint is protected with HTTP Basic Authentication. The query endpoints are open.

## Query Interfaces
In all query interfaces, the query parameter ‘supports-avails’ is optional. 
If absent, or if set to false, the response will include all attributes, whether they support PG avails or not. 
If set to true, the response will only include attributes that support PG avails, and attributes which have at least one PG Avails supporting value. 
In other words, if any attribute values contain avails-support: true, then the overall attribute must support avails.


##### Note on Pagination
Pagination is supported via query parameters page (starting page number, from zero) and size (page size i.e. number of array entries in ‘content’).


### Get attribute names

##### ```GET dim-val/api/v2/attr/names?supports-avails=true```

##### Query Parameters

| Parameter | Format | Required? | Description |
| --- | --- | --- | --- |
| account | string | no |  account id - use 'common' for base account 
| supports-avails | boolean | no | attribute-value pair is supported in PG Avails

##### Example Request 

```GET dim-val/api/v2/attr/names?supports-avails=true```

##### Expected Response

As shown [here](samples/get_all_attr_names_rsp.json).


##### Example Request 

`GET dim-val/api/v2/attr/names?account=1001&supports-avails=true`

##### Expected Response

As shown [here](samples/get_account_specific_attr_names_rsp.json).


### Get the value set for an exact attribute

##### `GET dim-val/api/v2/attr/values`

##### Query Parameters

| Parameter | Format | Required? | Description |
| --- | --- | --- | --- |
| account | string | yes |  account id - use 'common' for base account 
| attrId | string | yes |  attribute id 
| supports-avails | boolean | no | attribute-value pair is supported in PG Avails

##### Example Request 

`GET dim-val/api/v2/attr/values?account=1001&attrId=device.geo.ext.netacuity.country&supports-avails=true`

##### Expected Response

As shown [here](samples/get_account_specific_attr_values_rsp.json).


### Get the value set for an attribute matching regular expression with ```*``` and ```?```

##### `GET dim-val/api/v2/attr/values`

##### Query Parameters

| Parameter | Format | Required? | Description |
| --- | --- | --- | --- |
| account | string | yes |  account id - use 'common' for base account 
| attrId | string | yes |  attribute id using * or ? as wildcard
| supports-avails | boolean | no | attribute-value pair is supported in PG Avails

##### Example Request 

`GET dim-val/api/v2/attr/values?account=1001&attrId=device.*&supports-avails=true`

##### Expected Response

As shown [here](samples/get_account_specific_regex_attr_values_rsp.json).


### Get the value set for an attribute based on a linkedAttrId value

##### `GET dim-val/api/v2/attr/values`

##### Query Parameters

| Parameter | Format | Required? | Description |
| --- | --- | --- | --- |
| account | string | yes |  account id - use 'common' for base account 
| attrId | string | yes |  attribute id using * or ? as wildcard
| attrLinkValue | string | yes | query the attrId based on this link value
| supports-avails | boolean | no | attribute-value pair is supported in PG Avails

##### Example Request 

`GET dim-val/api/v2/attr/values?account=1001&attrId=device.geo.ext.netacuity.region&attrLinkValue=India&supports-avails=true`

##### Expected Response

As shown [here](samples/get_account_specific_attr_values_based_on_link_rsp.json).


### Query the value set display for an attribute based on a search string

##### `GET dim-val/api/v2/attr/search`

##### Query Parameters

| Parameter | Format | Required? | Description |
| --- | --- | --- | --- |
| account | string | yes |  account id - use 'common' for base account 
| attrId | string | yes |  attribute id
| valueSetDisplayNameSearchString | string | true | search string - no wildcards
| searchType | string | true | Attribute name 'contains' or 'startsWith' this parameter value
| supports-avails | boolean | no | attribute-value pair is supported in PG Avails


##### Example Request 

`GET dim-val/api/v2/attr/search?account=1000&attrId=device.geo.ext.netacuity.region&valueSetDisplayNameSearchString=Carolina&searchType=contains&supports-avails=true`

##### Expected Response

As shown [here](samples/get_account_specific_attr_values_search_contains_rsp.json).


##### Example request 

`GET dim-val/api/v2/attr/search?account=1001&attrId=device.geo.ext.netacuity.region&valueSetDisplayNameSearchString=North&searchType=startsWith&supports-avails=true`

##### Expected Response

As shown [here](samples/get_account_specific_attr_values_search_starts_with_rsp.json).


### No data found cases

Zero results will be returned with an empty ‘content’ tag.

Example response: 
```
{
   "content":[
      
   ],
   "page":{
      "size":20,
      "totalElements":0,
      "totalPages":0,
      "number":0
   }
}
```

## Uploading PG Targeting Attributes

This endpoint is only relevant to PG Host Companies who populate the targeting values.

The upload REST API works off of file attachments - attach a file and submit request, along with HTTP Basic Authentication header, 
as shown in the following curl example - 
```
curl -H 'Authorization: Basic dXNlcjI6cGFzc3dvc123' -F 'file=@country.csv' "http://localhost:8080/dim-val/api/v2/attr/upload"
```

#### File payload

File payloads are csv files, with 8 columns, as in this example [datasheet](samples/os_name.csv).
