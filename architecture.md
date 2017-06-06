Core logic:  
Main - start jetty server and init application context  
Config - properties file wrapper  
ChainGearDataLoader - operate with remote ChainGear to represent data in appropriate way  
MarketProcessor - schedule tasks for load trade history order books and reload market pairs  

Model:  
...  

Markets processing:  
Market - abstract class to process represented exchange  
Markets - Specific exchange collection  
TradeGetter - interface to load trades  
Several TradeGetter implementations  
MarketLoadTrigger, TradeLoadTask, OrderLoadTask - accessory classes to neat Markets and MarketProcessor  

Dabase services:  
ElasticsearchService - elasticsearch service  
RethinkDbService - rethinkDB service  

Rest:  
MainController - REST service request mapping  


