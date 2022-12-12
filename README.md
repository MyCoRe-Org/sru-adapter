# SRUAdapter

This maven module provides methods to set parameters
needed to communicate with an SRU interface. See the following example to get
an idea of how to use this connector or visit https://www.dnb.de/DE/Professionell/Metadatendienste/Datenbezug/SRU/sru.html

### Example

```
SRUConnector sru = new SRUConnector("https://services.dnb.de/sru");
sru.setCatalog(SRUConnector.CATALOG_DNB);
sru.setVersion(SRUConnector.VERSION_1_1);
sru.setOperation(SRUConnector.OPERATION_SEARCH_RETRIEVE);
sru.setQuery("WOE%3DGoethe");
sru.setRecordSchema(SRUConnector.RECORD_SCHEMA_MARC21_XML);
```

Invoking
```SRUConnector#getQueryURL()```
leads to the following url
```https://services.dnb.de/sru/dnb?version=1.1&operation=searchRetrieve&query=WOE%3DGoethe&recordSchema=MARC21-xml```
