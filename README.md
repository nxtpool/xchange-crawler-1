[![Travis branch](https://img.shields.io/travis/cyberFund/xchange-crawler/master.svg)](https://travis-ci.org/cyberFund/xchange-crawler)
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
cd crawler/target
java -jar xchange-crawler-0.1.0.jar
```

After first run crawler.properties file would be created the same folder as jar file.
```
rest.port - port for rest api
rethink.db.name - name of the rethink database
elastic.cluster.name - name of the elasticsearch cluster
elastic.node.host - elasticsearch localhost

```
