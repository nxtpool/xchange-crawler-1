# Xchange-crawler
### Xchange crawler for cryptoasset's markets with RethinkDB and ElasticSearch drivers

To get xchange module run submodule update:
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



