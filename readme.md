### @name getToken
POST http://localhost:8081/realms/url-shortener/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&
client_id=url-generator-service&
client_secret=my-secret&
username=dmytro&
password=pass
> {% client.global.set("access_token", response.body.access_token); %}

<> 2025-12-22T212955.200.json
<> 2025-12-22T203612.200.json
<> 2025-12-22T203204.200.json

####
#POST http://localhost:8080/api/shorten
#Content-Type: application/json
#Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJM1l5VkEyVUZKM2M2MVJjRVl4ZHBhMU5HQURrZ0UyS21kQXVtYVBMSlRBIn0.eyJleHAiOjE3NjY0Mjg4NzIsImlhdCI6MTc2NjQyODU3MiwianRpIjoiNDc2NzljMjItN2EyYi00OThlLTk2ZTUtMzg5MTQ3ZDNhYjY3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy91cmwtc2hvcnRlbmVyIiwic3ViIjoiOGE0N2JhMzgtM2UxMS00NDEwLWI5NzMtOTEyYzUwNzA3MDZkIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidXJsLWdlbmVyYXRvci1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6Ijg4MjlhNDU1LWEzODUtNDU0NC04MDIwLTQyZmRlMjdmNjljZCIsInNjb3BlIjoicm9sZXMtbWFwcGVyIiwic2lkIjoiODgyOWE0NTUtYTM4NS00NTQ0LTgwMjAtNDJmZGUyN2Y2OWNkIn0.Zv4VERIZxJoYmh_EkhQigEuELwNBbfK0_GpKt2rXeos4LxJwhZclNTjkmN4VzMWkSGOuKJQLqn66UcGHCp-OOrAgHrYa8OIv8zAhckYmNZh1Sl7-St45DHhV197goBReEHKJd76SZJF8aKAhIA38oIKvfNJBy20lrYMvCecUn0FTnMtCzd7BG3hHIdaAqVwkp76DHu_0Nn98DR8wRPAEqEyPvW0iyf1hohceJleAKjuYv1i3yuMH5gmNjvLXOtkTu_mNv4nb7Wnh4mCtZMKDj_XjrvDykhLQ448AaHUWRnOrVnQ1JgOVcblwdZbWjdE_iVo8OJzUqPU8UgFqAkvovw
#
#{
#  "url": "https://google.com"
#}


###
GET http://localhost:8080/api/departments
Content-Type: application/json
Authorization: Bearer {{access_token}}




<> 2025-12-22T213035.200.json
<> 2025-12-22T213001.500.json
<> 2025-11-25T233250.201.txt
<> 2025-11-25T233245.201.txt
<> 2025-11-25T232939.201.txt
<> 2025-11-25T232931.201.txt
<> 2025-11-25T232656.201.txt
<> 2025-11-25T232654.201.txt
<> 2025-11-25T232645.201.txt
<> 2025-11-25T231552.503.json
<> 2025-11-25T231532.503.json
<> 2025-11-25T231421.201.txt

###
GET http://localhost:8082/api/get
Authorization: Basic dmytro pass

###
GET http://localhost:8080/redirect/12D

###
GET http://localhost:8080/api/shorten/trigger-rest

###
GET http://localhost:8080/api/shorten/trigger-ws

###
GET http://localhost:8085/api/admin/send-manual?message=777

#Get Code
###
http://localhost:8081/realms/url-shortener/protocol/openid-connect/auth?response_type=code&client_id=url-generator-service&redirect_uri=http://localhost:8082&scope=openid&state=abc12345

#Get Access Token with code
###
POST http://localhost:8081/realms/url-shortener/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&
client_id=url-generator-service&
client_secret=my-secret&
code=
12789672-0175-4f56-9a99-550122c5be36.527f8714-51d7-4cfc-a433-3431a183d5ed.e16e2c00-58d4-48c7-b841-85ccc9a99943
&
redirect_uri=http://localhost:8082

#Get Access Token with password
###
# getToken.http
POST http://localhost:8081/realms/url-shortener/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&
client_id=url-generator-service&
client_secret=my-secret&
username=dmytro&
password=pass

> {% client.global.set("access_token", response.body.access_token); %}

<> 2025-12-22T202651.200.json





###
GET http://localhost:8080/test/api/with/certificate

<> 2025-12-07T120837.200.txt
<> 2025-12-06T215420.200.txt
<> 2025-12-06T214857.500.json
<> 2025-12-06T213207.500.json
<> 2025-12-06T211658.500.json
<> 2025-12-06T195412.200.txt
<> 2025-12-06T195253.500.json

###
GET http://localhost:8080/api/departments
Content-Type: application/json

{
"id": 0,
"name": ""
}

<> 2025-12-22T193746.200.json
<> 2025-12-21T124811.200.json
<> 2025-12-07T102449.200.json

###
POST http://localhost:8081/users
Content-Type: application/json

{
"firstName": "rrr2",
"lastName": "rrr2",
"email": "rrr2@ggg.com",
"password": "rrrdddddd"
}

<> 2025-12-20T105343.201.json
<> 2025-12-20T105329.201.json
<> 2025-12-20T094213.201.json
<> 2025-12-20T093809.201.json
<> 2025-12-20T093801.201.json
<> 2025-12-20T093738.400.json
<> 2025-12-20T093722.400.json

###
GET http://localhost:8081/users/694652614f08705631f17b02

<> 2025-12-20T150807.201.json
<> 2025-12-20T094520.201.json
<> 2025-12-20T094431.201.json
<> 2025-12-20T094417.201.json
<> 2025-12-20T094345.201.json
<> 2025-12-20T094321.201.json
<> 2025-12-20T094313.201.json
<> 2025-12-20T094235.400.json

###
GET http://localhost:8081/users
Content-Type: application/json

{
"firstName": "",
"lastName": "",
"email": "",
"password": ""
}

<> 2025-12-21T091201.201.json
<> 2025-12-21T091139.201.json
<> 2025-12-21T091125.201.json
<> 2025-12-21T091100.201.json

###
GET http://localhost:8081/users/first
Content-Type: application/json

###
GET http://localhost:8081/users/threads

<> 2025-12-20T102951.201.txt
<> 2025-12-20T102849.500.json
<> 2025-12-20T102658.201.txt
<> 2025-12-20T101722.201.txt
<> 2025-12-20T101651.201.txt







Frontend

npm create vite@latest . -- --template react-ts
npm install
npm run dev

npm install axios