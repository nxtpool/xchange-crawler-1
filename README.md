[![Travis branch](https://img.shields.io/travis/cyberFund/xchange-crawler/master.svg)](https://travis-ci.org/cyberFund/xchange-crawler)
### [XChange]-based crawler for cryptoasset's markets

XChange-crawler polls implemented exchange markets (see #8 for
the list) and sends data to RethinkDB and ElasticSearch through
thier dedicated DB drivers.

#### Building from source

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

[XChange]: https://github.com/timmolter/XChange
