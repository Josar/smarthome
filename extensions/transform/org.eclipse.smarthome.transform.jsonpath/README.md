# JsonPath Transformation Service

Extract an element of a JSON string using a [JsonPath expression](https://github.com/jayway/JsonPath#jayway-jsonpath).

Returns `null` if the JsonPath expression could not be found.

// Small introduction of jsonpath
// Todo list differences which are unique for the openhab implementation
// returns evaluated elements not an array, Does not return a list when 

// show valid transformation
// Item label
// Binding channel
// rule

## Example

Given the JsonPath expression `$.device.status.temperature`:

| input | output |
|-------|--------|
| `{ "device": { "status": { "temperature": 23.2 }}}` | `23.2` |

## Further Reading
An extended (introduction)[https://www.w3schools.com/js/js_json_intro.asp] can be found at W3School.
As JsonPath transformation is based on [Jayway](https://github.com/json-path/JsonPath) using a [online validator](https://jsonpath.herokuapp.com/) which also uses Jaway will give almost identical results. 
