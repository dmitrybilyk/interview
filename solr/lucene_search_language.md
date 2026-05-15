## Notes:
- Lucene is case-insensitive by default
- 

1. Find word occurrence in the `title` field of all documents 

title_t: boot 

2. Find one or another by in field's values:

title_t: search with solr

3. Find documents where in field there is exact match:

title_t: "search with solr"

4. Find with combined logical operators: AND, OR, NOT

- category_s:architecture AND title_t:Search

- title_t:Solr NOT category_s:devops

5. Find with masking: ? or *

description_t: sc*
description_t: scal?

6. Find with range:

- inclusive
  price:[30.00 TO 45.00]
- exclusive
  price:{30.00 TO 45.00}

7. Find with not exact occurrence:

title_t: sear~