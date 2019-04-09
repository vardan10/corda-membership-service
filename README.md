# Corda Membership Service

More about this [here](https://solutions.corda.net/designs/business-networks-membership-service.html)

## Instructions for setting up
1. `git clone https://github.com/vardan10/corda-membership-service.git`
2. `cd corda-membership-service`
3. `./gradlew deployNodes` - building may take upto a minute (it's much quicker if you already have the Corda binaries).
4. `bash createMembershipConfig.sh`
5. `./workflows-kotlin/build/nodes/runnodes`
6. `./gradlew runPartyAServer` (do in a new terminal)
7. `./gradlew runBnoServer` (do in a new terminal)

## Using the CorDapp via the API:
1. Create IOU (Should fail as membership is not active)
```
curl -X POST \
  http://localhost:50005/api/example/create-iou \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'iouValue=99&partyName=O%3DPartyB%2CL%3DNew%20York%2CC%3DUS'
```

2. Request For membership
```
curl -X POST \
  http://localhost:50005/api/membership/request \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d role=DEFAULT
  -d name='O=BNO,L=New York,C=US'
  ```

3. Activate membership
```
curl -X POST \
  http://localhost:50008/api/membership/activate \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d name=O%3DPartyA%2CL%3DLondon%2CC%3DGB
```

4. Create IOU (Should create successfully)
```
curl -X POST \
  http://localhost:50005/api/example/create-iou \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'iouValue=99&partyName=O%3DPartyB%2CL%3DNew%20York%2CC%3DUS'
```

5. Get Membership
```
curl -X POST \
  http://localhost:50005/api/membership/get \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d name=O%3DBNO%2CL%3DNew%20York%2CC%3DUS
```

6. Change Party Metadata
```
curl -X POST \
  http://localhost:50005/api/membership/ammend \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'role=DEFAULT2&name=O%3DBNO%2CL%3DNew%20York%2CC%3DUS'
```

7. Revoke membership
```
curl -X POST \
  http://localhost:50008/api/membership/revoke \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d name=O%3DPartyA%2CL%3DLondon%2CC%3DGB
```

8. Create IOU (Should fail again)
```
curl -X POST \
  http://localhost:50005/api/example/create-iou \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'iouValue=99&partyName=O%3DPartyB%2CL%3DNew%20York%2CC%3DUS'
```



Corda distribution service
```
start ScheduleSyncFlow syncerConfig: null, launchAsync: true
```