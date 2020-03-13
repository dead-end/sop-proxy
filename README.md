# Sop-Proxy

The Sop-Proxy is a program that allows a javascript application to access 
data on different servers. It is an easy to use proxy that adresses the 
restrictions of the 
[same-origin-policy](https://en.wikipedia.org/wiki/Same-origin_policy).

The application is started with a csv file, which contains definitions of the
remote targets, that should be reached. Each target has at least an id and an
url and for each target a proxy servlet is created with the id as part of the
context path.

Additionally the Sop-Proxy has a servlet the serves the content of a configured
directory. The directory contains the javascript application (html, css, javascript, ...).

The last part of the Sop-Proxy is a config servlet, which provides a simple
REST api for the configured proxies.

![Sop-Proxy](resources/sop-proxy.png)

# Example
## Remote URL's
For the following example we are using these two urls, which return json data:

- https://www.weatherapi.com/docs/weather_conditions.json
- https://www.googleapis.com/books/v1/volumes?q=isbn:1492037257

Both urls are using ssl, so we need to create a trust store with their certificates:

```
keytool -import -file storage-googleapis.pem -alias storage-googleapis -keystore sop-proxy-trust-store -storepass changeit
keytool -import -file weatherapi-com.pem -alias weatherapi-com -keystore sop-proxy-trust-store -storepass changeit
```

## CSV File
We create a csv file with the proxy configurations:

```csv
id,url,name
weather,https://www.weatherapi.com/docs/,Weather
books,https://www.googleapis.com/books/v1/,Books
```

For each entry in the csv file, a proxy servlet is created. If the sop-proxy runs on
localhost:8080, then the incoming requests are mapped in the following manner.

|Lokal URL|Remote URL|
|---|---|
|http://localhost:8080/proxy/weather/|https://www.weatherapi.com/docs/|
|http://localhost:8080/proxy/weather/weather_conditions.json|https://www.weatherapi.com/docs/weather_conditions.json|
|http://localhost:8080/proxy/books/|https://www.googleapis.com/books/v1/|
|http://localhost:8080/proxy/books/volumes?q=isbn:1492037257|https://www.googleapis.com/books/v1/volumes?q=isbn:1492037257|


## Config Servlet

http://localhost:8080/config/list

```json
[
  {
    "id": "books",
    "url": "https://www.googleapis.com/books/v1/",
    "data": [
      {
        "name": "Books"
      }
    ]
  },
  {
    "id": "weather",
    "url": "https://www.weatherapi.com/docs/",
    "data": [
      {
        "name": "Weather"
      }
    ]
  }
]
```
http://localhost:8080/config/get/books

```json
{
  "id": "books",
  "url": "https://www.googleapis.com/books/v1/",
  "data": [
    {
      "name": "Books"
    }
  ]
}
```
