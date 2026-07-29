./gradlew clean bootJar

docker build -f docker/Dockerfile -t lightspeed-app .

docker run -e ENVIRONMENT=dev -p 8080:8080 lightspeed-app
