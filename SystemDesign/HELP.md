docker-compose up --build

curl http://localhost:8080/hello


[//]: # (    sudo apt-get install apache2-utils)
# 100 requests, concurrency 20
ab -n 100 -c 20 http://localhost:8080/hello


Change upstream spring_backend strategy to least_conn; or ip_hash; and restart NGINX:
docker-compose restart nginx
