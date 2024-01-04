GET TOKEN:(authorization server)

curl -iX POST http://192.168.102.82:8092/oauth2/token -d "client_id=1110000024&client_secret=1110000024&grant_type=client_credentials"

USE TOKEN(gateway):

curl -iX POST http://192.168.102.82:8090/push-service/test -H "Authorization: Bearer eyJraWQiOiJhOWJjZj..."