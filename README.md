# XChange-crawler
### Crawler for cryptoasset's markets using XChange library

XChange-crawler polls implemented exchange markets (see #8 for
the list) and sends data to RethinkDB and ElasticSearch through
thier dedicated DB drivers.

Before building crawler, let git fetch XChange sources:
```bash
git submodule init
git submodule update

```

To build and run crawler:
```bash
mvn install
cd crawler
mvn exec:java

```

Properties stored in src/main/resorces/crawler.properties
```
rethink.db.name - name of the rethink database
elastic.cluster.name - name of the elasticsearch cluster
elastic.node.host - elasticsearch localhost

```


####Prerequisites
Java, Maven, RthinkDB, Elasticsearch

