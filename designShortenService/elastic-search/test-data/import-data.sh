# First, check how many lines are in your file
wc -l nginx-access-logs-2020-03.bulk.ndjson

# Split into smaller chunks (5000 documents = 10000 lines since bulk format has 2 lines per doc)
split -l 10000 nginx-access-logs-2020-03.bulk.ndjson nginx-part-

# Import each chunk
for file in nginx-part-*; do
  echo "Importing $file..."
  curl -H "Content-Type: application/x-ndjson" \
       -XPOST "http://localhost:9200/_bulk?refresh=wait_for" \
       --data-binary "@$file"
  echo ""
  sleep 1
done

# Clean up
rm nginx-part-*