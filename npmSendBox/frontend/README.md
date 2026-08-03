# frontend

React (Vite) frontend served by nginx inside Docker.

## Build the image

```bash
cd npmSendBox/frontend
docker build -t sendbox-frontend .
```

## Run the container

```bash
docker run -p 8080:80 --name sendbox-frontend sendbox-frontend
```

## Verify

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/
```

Expected output:

```
200
```

Then open http://localhost:8080 in your browser.

## Local development (without Docker)

```bash
cd npmSendBox/frontend
npm install
npm run dev
```

## Stop / remove

```bash
docker rm -f sendbox-frontend
```
