minikube service spring-webflux --url

kubectl exec -it deploy/spring-webflux -- curl http://localhost:8080/hello
minikube service spring-webflux-demo -n demo --url
curl http://192.168.49.2:32373/hello
curl http://192.168.49.2:32614/hello


helm upgrade spring-webflux ./spring-webflux

docker build -t spring-webflux:0.0.1 .

kubectl config view --minify --output 'jsonpath={..namespace}'

helm install spring-webflux-demo ./spring-webflux -n demo

kubectl config set-context --current --namespace=demo




