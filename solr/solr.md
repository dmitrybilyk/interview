Lucene → Java library that actually does indexing and searching.
Solr and Elasticsearch → server applications built on top of Lucene.
Kibana → visualization/UI tool mainly for Elasticsearch.

Search languages → ways to ask Solr/Elasticsearch for results
# Apache Solr & Lucene: Interview Cheat Sheet

## 1. What is Apache Solr & Lucene?
* **Apache Lucene** is a low-level, high-performance Java search library. It owns the core data structures on disk, handles text analysis, builds the indexes,
  and calculates relevance scores. It is just a library (`.jar`), meaning it has no network interface, no server capabilities, and no UI.
* **Apache Solr** is an enterprise-grade search server built around Lucene. It wraps the Lucene engine and provides a REST API, an Admin UI, security, advanced
  query parsing (DisMax/eDisMax), caching, and client libraries like **SolrJ** for Java/Kotlin integration.

---

## 2. Solr Configuration: Schema & SolrConfig
Solr is highly customizable, and its behavior is defined by two main configuration components:

### A. What is Solr Schema?
The **Schema** defines the structure of your data (similar to a table definition in SQL). It tells Solr what fields exist and how they should be treated.
* **Fields:** Defines name, type (e.g., `string`, `tint`, `text_general`), and properties:
   * `indexed="true"`: Means Lucene will put this field into the Inverted Index, making it searchable.
   * `stored="true"`: Means Lucene will keep the raw value in the Stored Fields blob, making it visible in the search results.
   * `docValues="true"`: Enriches the field with a columnar structure, making it ready for sorting and faceting.
* **Dynamic Fields:** Allows indexing fields that aren't explicitly declared, using wildcards (e.g., `*_txt` automatically maps to a text type).

### B. How to Configure Solr
Solr configuration can be managed in two ways depending on the deployment mode:
1.  **Classic XML Files:**
   * `schema.xml` (or `managed-schema.xml`): Defines fields, field types, and text analyzers.
   * `solrconfig.xml`: Controls server-level settings like caches, request handlers (e.g., mapping `/select` to a specific query parser), and update processors.
2.  **Configsets & ZooKeeper (SolrCloud Mode):**
   * In a distributed environment, configurations are grouped into **Configsets** and uploaded to **Apache ZooKeeper**. Every node in the cluster reads
     configurations from ZooKeeper to stay synchronized.

---

## 3. How Data is Stored: Lucene's 3 Disk Structures
When you send a JSON or XML document over HTTP, Solr validates it against the schema, extracts the raw data, and hands it to Lucene. Lucene completely shatters
the document and stores it in three distinct binary formats on the file system:

* **Inverted Index (The Search Map):** Maps `Term -> List of Doc IDs`. It is used exclusively to find which documents match the query and calculate the score.
* **Stored Fields Blob (The Raw Payload):** A compressed, row-oriented binary blob containing the original text. Lucene completely ignores this during the search
  phase. Only after the Inverted Index identifies the matching Doc IDs, Lucene decompresses these specific rows to return the original fields to Solr.
* **DocValues Blob (The Columnar Data):** A column-oriented binary structure that groups values of a single field (like `price` or `category`) across all
  documents. It is optimized for in-memory columnar operations like sorting, faceting, and mathematical functions.

---

## 4. The Text Analysis Pipeline (How Inverted Index is Built)
Before text fields hit the Inverted Index, Lucene processes them through a strict analysis pipeline defined in the schema:
1.  **Tokenization:** Breaks raw sentences into individual words/tokens (e.g., `"Spring with SolrJ"` becomes `[Spring]`, `[with]`, `[SolrJ]`).
2.  **Lowercasing:** Converts all tokens to lowercase so that searches for `"SOLR"` and `"solr"` yield identical results.
3.  **Stop-words Filtering:** Drops common, non-semantic words (e.g., `"with"`, `"is"`, `"a"`, `"the"`) to prevent index bloat.
4.  **Stemming/Morphology:** Reduces words to their base root (e.g., `"Techniques"` and `"technical"` both map to `techniqu`), allowing a query for `technique`
    to match a document containing `"Techniques"`.

Apache Solr — це open-source платформа для повнотекстового пошуку, побудована на базі бібліотеки Apache Lucene.

Ключова ідея: На відміну від реляційних БД (де ми шукаємо по рядках), 
Solr використовує Inverted Index (інвертований індекс). 
Це схоже на покажчик у кінці книги: замість того, щоб гортати всі сторінки, 
ти дивишся слово "Spring" і бачиш список сторінок (ID документів), де воно зустрічається.

Чому не SQL? SQL погано справляється з нечітким пошуком, морфологією (шукаємо "running" — знаходимо "run") 
та ранжуванням результатів за релевантністю. Solr робить це блискавично.
---

## 5. Division of Labor: Feature Breakdown

| Feature Type | Feature Name | Who actually owns it? | Description |
| :--- | :--- | :--- | :--- |
| **Search & Indexing** | Full-Text Search & Relevance | 🔹 **Pure Lucene** | Low-level Inverted Index management and BM25 scoring. |
| **Search & Indexing** | Fuzzy Search & Spellchecking | 🔹 **Pure Lucene** | Native Levenshtein distance calculations on text tokens. |
| **Search & Indexing** | Analysis Pipeline | 🔹 **Pure Lucene** | Underlying Java Tokenizers and Filters that normalize text. |
| **UI & APIs** | Admin UI & HTTP REST API | 🔸 **Pure Solr** | The web application layer that allows interacting with Lucene. |
| **UI & APIs** | Advanced Query Parsers | 🔸 **Pure Solr** | DisMax/eDisMax parsers that accept raw user text and map it to fields. |
| **Data Analytics** | Faceting & Highlighting | 🤝 **Joint Effort** | Lucene provides fast doc lookups; Solr aggregates counts and wraps tags. |
| **Infrastructure** | Distributed Scalability | 🔸 **Pure Solr** | Sharding, Replication, and cluster orchestration via ZooKeeper. |