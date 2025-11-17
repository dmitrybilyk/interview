snap install --edge grpcurl

grpcurl -plaintext localhost:9090 list

grpcurl -plaintext \
-d '{"shortCode": "abc123"}' \
localhost:9090 redirector.RedirectorService/Resolve
