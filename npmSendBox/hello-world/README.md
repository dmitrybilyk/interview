# hello-world

Express HTTP server with a `/` (Hello World) and `/health` endpoint.

## Build the image

```bash
cd npmSendBox/hello-world
docker build -t sendbox-hello-world .
```

## Run the container

```bash
docker run -p 3000:3000 --name sendbox-hello-world sendbox-hello-world
```

## Verify

```bash
curl http://localhost:3000/
curl http://localhost:3000/health
```

Expected output:

```
Hello World from npmSendBox!
{"status":"ok"}
```

## Stop / remove

```bash
docker rm -f sendbox-hello-world
```
