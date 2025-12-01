#!/bin/bash

# Wait for Elasticsearch
echo "Waiting for Elasticsearch..."
until curl -s http://localhost:9200/_cluster/health | grep -q '\"status\":\"green\"'; do
  sleep 5
done

# Create templates
curl -X PUT http://localhost:9200/_index_template/url-template \
  -H 'Content-Type: application/json' \
  -d '{
    "index_patterns": ["urls-*"],
    "template": {
      "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 0
      },
      "mappings": {
        "properties": {
          "short_url": { "type": "keyword" },
          "original_url": { "type": "text" },
          "created_at": { "type": "date" },
          "user_id": { "type": "keyword" },
          "click_count": { "type": "integer" },
          "tags": { "type": "keyword" }
        }
      }
    }
  }'

# Create indices
curl -X PUT http://localhost:9200/urls-2024

# Add test data
curl -X POST http://localhost:9200/urls-2024/_bulk \
  -H 'Content-Type: application/json' \
  --data-binary '
{"index":{}}
{"short_url":"abc123","original_url":"https://example.com/page1","created_at":"2024-01-01T10:00:00Z","user_id":"user1","click_count":42,"tags":["public","example"]}
{"index":{}}
{"short_url":"def456","original_url":"https://example.com/page2","created_at":"2024-01-02T11:00:00Z","user_id":"user2","click_count":15,"tags":["private"]}
{"index":{}}
{"short_url":"ghi789","original_url":"https://example.com/page3","created_at":"2024-01-03T12:00:00Z","user_id":"user1","click_count":87,"tags":["public","test"]}
'

echo "Elasticsearch setup complete!"