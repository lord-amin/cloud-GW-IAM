Full feature spring OAuth2 authorization server
-

- Use consul for discovery server
- make it possible to cache users in heap . use rabbit for event sourcing in multiple instances- 
- make it possible to cache users in REDIS

Use JWT for authorization:
-
- make it possible to use cloud config server for public and private key
- make it possible to use local config for public and private key

Use custom load balancer:
- 
- use WebClient to calling remote call . configure load balancer


GET TOKEN:
- 
curl -iX POST http://192.168.102.82:8092/oauth2/token -d "client_id=1110000024&client_secret=1110000024&grant_type=client_credentials"

USE TOKEN:
-

curl -iX POST http://192.168.102.82:8090/push-service/test -H "Authorization: Bearer eyJraWQiOiJhOWJjZj..."