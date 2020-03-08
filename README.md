# sop-proxy

# Example
For the following example we are using these two urls, which return json data:

- https://www.googleapis.com/books/v1/volumes?q=isbn:1492037257
- https://www.weatherapi.com/docs/weather_conditions.json

We create a csv file with the proxy configurations:

```csv
id,url,name
weather,https://www.weatherapi.com/docs/,Weather
books,https://www.googleapis.com/books/v1/,Books
```  

Both urls are using ssl, so we need to create a trust store with their certificates:

```  
keytool -import -file storage-googleapis-com.pem -alias storage-googleapis-com -keystore sop-proxy-trust-store -storepass changeit
keytool -import -file weatherapi-com.pem -alias weatherapi-com -keystore sop-proxy-trust-store -storepass changeit
```
